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

    @PostUpdate
    public void onPostUpdate(){
        Canceled canceled = new Canceled();
        BeanUtils.copyProperties(this, canceled);
        canceled.publishAfterCommit();


    }


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
