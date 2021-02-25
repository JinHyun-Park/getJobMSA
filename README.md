# 구직 시스템

# 목차
  - [구직 시스템](#---)
    - [시나리오](#시나리오)
    - [기능적 요구사항](#기능적-요구사항)
    - [비기능적 요구사항](#비기능적-요구사항)
    - [분석 설계](#분석-설계)
    - [헥사고날 아키텍처](#헥사고날-아키텍처)
  - [구현](#구현)
    - [DDD의 적용](#DDD의-적용)
    - [API gateway](#API-gateway)
    - [폴리그랏 퍼시스턴스](#폴리그랏-퍼시스턴스)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기 호출](#비동기-호출)
    - [Saga Pattern / 보상 트랜잭션](#Saga-Pattern--보상-트랜잭션)
    - [CQRS](#CQRS)
  - [운영](#운영)
    - [Liveness / Readiness 설정](#Liveness--Readiness-설정)
    - [Self Healing](#Self-Healing)
    - [CI/CD 설정](#CICD설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출--서킷-브레이킹--장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [ConfigMap / Secret](#ConfigMap--Secret)

## 시나리오

구직 시스템에서 요구하는 기능/비기능 요구사항은 다음과 같습니다. 사용자가 구직 등록을 진행하면 자격증명을 확인하고 최종 등록이 된다.

### 기능적 요구사항

1. 구직자가 자신의 정보를 등록한다.
2. 등록된 정보를 바탕으로 자격 정보를 확인한다.
3. 구직자의 정보가 등록된다.
4. 구직자의 정보 등록을 취소할 수 있다.
5. 등록된 정보가 취소 된다.

### 비기능적 요구사항 
1. 트랜잭션
   - 구직 등록이 되지 않은 건은 자격 확인이 되지 않아야 한다. `Sync 호출`

2. 장애격리
   - 자격 확인 기능이 수행 되지 않더라도 등록은 365일 24시간 받을 수 있어야 한다. `Pub/Sub`
   - 등록 시스템이 과중되면 사용자를 잠시동안 받지 않고 자격 확인을 잠시후에 하도록 유도 한다.
     (장애처리)

3. 성능
   - 고객이 예약 확인 상태를 마이페이지에서 확인할 수 있어야 한다. `CQRS`

### 분석 설계
- Event Storming model
![설계](https://user-images.githubusercontent.com/17754849/109106604-7acafd80-7773-11eb-9932-5cff3676a59c.png)

### 헥사고날 아키텍처
![헥사](https://user-images.githubusercontent.com/17754849/109106646-90402780-7773-11eb-8dc7-d8d2e1b7e451.JPG)

## 구현

### DDD의 적용
- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다
```
package getjob;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Recruitment_table")
public class Recruitment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String skill;
    private String job;
    private String status;

    @PostPersist
    public void onPostPersist(){
        Applied applied = new Applied();
        BeanUtils.copyProperties(this, applied);
        applied.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        getjob.external.Qualification qualification = new getjob.external.Qualification();
        // mappings goes here

        qualification.setRecruitmentId(this.getId());
        qualification.setJob(this.getJob());
        qualification.setConfirmYn("Y");
        qualification.setStatus("Apply Job");

        RecruitmentApplication.applicationContext.getBean(getjob.external.QualificationService.class)
            .check(qualification);


    }
    ... 중략 ... 


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }
    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}

```
- 적용 후 REST API 의 테스트
![rest](https://user-images.githubusercontent.com/17754849/109106764-cf6e7880-7773-11eb-9b88-c045bdde2c93.png)

### API gateway
![api](https://user-images.githubusercontent.com/17754849/109106828-f0cf6480-7773-11eb-9408-ebeaa9524b94.png)
![api2](https://user-images.githubusercontent.com/17754849/109106892-13fa1400-7774-11eb-8a04-2d59c838af79.png)

### 폴리그랏 퍼시스턴스
- h2 와 hsqldb를 사용하였다.
![poly](https://user-images.githubusercontent.com/17754849/109107016-558abf00-7774-11eb-95ac-d0c5845304a8.png)

### 동기식 호출 과 Fallback 처리
- recruit 요청이 들어온 후 qualification 단계로 진행될 때 FeignClient를 이용해서 동기 방식으로 진행된다.
![feign](https://user-images.githubusercontent.com/17754849/109107399-fbd6c480-7774-11eb-9cfa-c84bc8e84262.png)

- Fallback 처리 : 동기일 경우 qualification이 종료된 경우 등록 불가
![fallback](https://user-images.githubusercontent.com/17754849/109107776-b1a21300-7775-11eb-86e6-8f36d407414d.png)
 
### 비동기 호출
- 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트
- 비동기로 qualification 에서 다음 enrollment로 카프카를 통해 호출되게 한다.
![비동기](https://user-images.githubusercontent.com/17754849/109108209-689e8e80-7776-11eb-8dac-439724edcee8.png)

- policyhandler가 카프카 메시지를 listen 하다가 해당 이벤트를 수신한다.
![폴리시](https://user-images.githubusercontent.com/17754849/109108316-a26f9500-7776-11eb-816f-2bfcec56d12a.png)

- 비동기 시 enrollment가 죽어있더라도 정상적으로 서비스를 수신한다.
![비동기콜](https://user-images.githubusercontent.com/17754849/109108498-085c1c80-7777-11eb-8e6c-8bee71b37590.png)

### Saga Pattern / 보상 트랜잭션
- 취소 요청이 들어온 경우 보상 트랜잭션을 통해 기존에 등록한 요청을 취소 상태로 변경한다.
![취소](https://user-images.githubusercontent.com/17754849/109108791-81f40a80-7777-11eb-9b9a-580f238fd24d.png)

### CQRS
- MyPage를 통해 DB join 없이 등록된 데이터를 조회할 수 있다.
![마이페이지](https://user-images.githubusercontent.com/17754849/109108976-e4e5a180-7777-11eb-9042-db290fcb0d82.png)

## 운영

### Liveness / Readiness 설정
![설정](https://user-images.githubusercontent.com/17754849/109109236-460d7500-7778-11eb-81bd-a054d4ed6602.png)
![설정2](https://user-images.githubusercontent.com/17754849/109109461-b87e5500-7778-11eb-86cb-2e15af7b51bf.png)

### Self Healing
- liveness Probe를 설정하여 문제 발생 시 스스로 재기동한다.
![부활](https://user-images.githubusercontent.com/17754849/109109335-748b5000-7778-11eb-8920-fdad2dad05e0.png)

### CI/CD 설정
- buildspec.yml 을 만들고 aws codebuild 를 통해 CICD 되도록 구성하였다.
```
version: 0.2

env: 
  variables:
    _PROJECT_NAME: "recruit-enrollment"
    _DIR_NAME: "enrollment"
    _EKS: "recruit"
    _NAMESPACE: "recruit"

phases:
  install:
    runtime-versions:
      java: openjdk8
      docker: 18
    commands:
#      - echo install kubectl
#      - curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
#      - chmod +x ./kubectl
#      - mv ./kubectl /usr/local/bin/kubectl
#      - echo eks --region $AWS_DEFAULT_REGION update-kubeconfig --name $_EKS
#      - aws eks --region $AWS_DEFAULT_REGION update-kubeconfig --name $_EKS
  pre_build:
    commands:
      - echo Logging in to Amazon ECR...
      - echo $_PROJECT_NAME
      - echo $AWS_ACCOUNT_ID
      - echo $AWS_DEFAULT_REGION
      - echo $CODEBUILD_RESOLVED_SOURCE_VERSION
      - echo start command
      - $(aws ecr get-login --no-include-email --region $AWS_DEFAULT_REGION)
  build:
    commands:
      - echo Build started on `date`
      - echo Building the Docker image...
      - cd $_DIR_NAME && mvn package -Dmaven.test.skip=true
      - ls -al
      - pwd
      - docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$_PROJECT_NAME:$CODEBUILD_RESOLVED_SOURCE_VERSION .
  post_build:
    commands:
      - echo Pushing the Docker image...
      - docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$_PROJECT_NAME:$CODEBUILD_RESOLVED_SOURCE_VERSION
      - echo connect kubectl
      - kubectl config set-cluster k8s --server="$KUBE_URL" --insecure-skip-tls-verify=true
      - kubectl config set-credentials admin --token="$KUBE_TOKEN"
      - kubectl config set-context default --cluster=k8s --user=admin
      - kubectl config use-context default
      - |
          cat <<EOF | kubectl apply -f -
          apiVersion: v1
          kind: Service
          metadata:
            name: $_DIR_NAME
            namespace: $_NAMESPACE
            labels:
              app: $_DIR_NAME
          spec:
            ports:
              - port: 8080
                targetPort: 8080
            selector:
              app: $_DIR_NAME
          EOF
      - |
          cat  <<EOF | kubectl apply -f -
          apiVersion: apps/v1
          kind: Deployment
          metadata:
            name: $_DIR_NAME
            namespace: $_NAMESPACE
            labels:
              app: $_DIR_NAME
          spec:
            replicas: 1
            selector:
              matchLabels:
                app: $_DIR_NAME
            template:
              metadata:
                labels:
                  app: $_DIR_NAME
              spec:
                containers:
                  - name: $_DIR_NAME
                    image: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$_PROJECT_NAME:$CODEBUILD_RESOLVED_SOURCE_VERSION
                    imagePullPolicy: Always
                    ports:
                      - containerPort: 8080
                    readinessProbe:
                      httpGet:
                        path: /actuator/health
                        port: 8080
                      initialDelaySeconds: 10
                      timeoutSeconds: 2
                      periodSeconds: 5
                      failureThreshold: 10
                    livenessProbe:
                      httpGet:
                        path: /actuator/health
                        port: 8080
                      initialDelaySeconds: 120
                      timeoutSeconds: 2
                      periodSeconds: 5
                      failureThreshold: 5
          EOF
cache:
  paths:
    - '/root/.m2/**/*'
```
![환경](https://user-images.githubusercontent.com/17754849/109109740-33477000-7779-11eb-8cdd-58cd2133d54a.png)
![성공](https://user-images.githubusercontent.com/17754849/109109755-3a6e7e00-7779-11eb-88ec-c2c1f7859ed7.png)


### 동기식 호출 / 서킷 브레이킹 / 장애격리
- FeignClient + hystrix
![적용1](https://user-images.githubusercontent.com/17754849/109110104-d4362b00-7779-11eb-98d8-71fdd33a47c6.png)
![적용2](https://user-images.githubusercontent.com/17754849/109110114-d6988500-7779-11eb-965d-a01f1aa55ba2.png)
![처리](https://user-images.githubusercontent.com/17754849/109110173-f2039000-7779-11eb-80fc-bcdf9ade4a3d.png)

### 무정지 배포
- readiness probe 를 통해 이후 서비스가 활성 상태가 되면 유입을 진행시킨다.
![롤링](https://user-images.githubusercontent.com/17754849/109110673-e49ad580-777a-11eb-81c4-5f92a0ae4f49.png)
![유입](https://user-images.githubusercontent.com/17754849/109110679-e6fd2f80-777a-11eb-9db6-ecac77cb6fae.png)
![이전꺼다이](https://user-images.githubusercontent.com/17754849/109110733-08f6b200-777b-11eb-8029-7f1c73ffcfd7.png)

### 오토스케일 아웃
- replica를 동적으로 늘려서 HPA를 설정한다.
![설정](https://user-images.githubusercontent.com/17754849/109110852-3cd1d780-777b-11eb-99a2-30ff2a8a80e6.png)
![확장전](https://user-images.githubusercontent.com/17754849/109110855-3e030480-777b-11eb-98f5-d8879f28924a.png)
![확장후](https://user-images.githubusercontent.com/17754849/109110866-40655e80-777b-11eb-8b5a-4fb3348b8423.png)

### ConfigMap / Secret
- configmap과 secret으로 환경변수를 설정하였다.
![컨피그1](https://user-images.githubusercontent.com/17754849/109110983-786ca180-777b-11eb-8f80-0e5648e53d1d.png)
![시크릿1](https://user-images.githubusercontent.com/17754849/109110975-74d91a80-777b-11eb-95e8-f8d08963de9d.png)

- 환경변수 설정
![변수](https://user-images.githubusercontent.com/17754849/109110986-79053800-777b-11eb-819f-ff4be476ef12.png)
![출력](https://user-images.githubusercontent.com/17754849/109110979-773b7480-777b-11eb-8917-cb8de6c3bf6c.png)
