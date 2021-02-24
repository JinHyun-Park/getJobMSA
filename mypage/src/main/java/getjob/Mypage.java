package getjob;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Mypage_table")
public class Mypage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long recruitmentId;
        private Long qualificationId;
        private Long enrollmentId;
        private String job;
        private String name;
        private Integer age;
        private String skill;
        private String confirmYn;
        private String status;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        public Long getRecruitmentId() {
            return recruitmentId;
        }

        public void setRecruitmentId(Long recruitmentId) {
            this.recruitmentId = recruitmentId;
        }
        public Long getQualificationId() {
            return qualificationId;
        }

        public void setQualificationId(Long qualificationId) {
            this.qualificationId = qualificationId;
        }
        public Long getEnrollmentId() {
            return enrollmentId;
        }

        public void setEnrollmentId(Long enrollmentId) {
            this.enrollmentId = enrollmentId;
        }
        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
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
        public String getConfirmYn() {
            return confirmYn;
        }

        public void setConfirmYn(String confirmYn) {
            this.confirmYn = confirmYn;
        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

}
