package getjob;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Enrollment_table")
public class Enrollment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long recruitmentId;
    private Long qualificationId;
    private String job;
    private String status;
    private String confirmYn;

    @PostPersist
    public void onPostPersist(){
        Registered registered = new Registered();
        BeanUtils.copyProperties(this, registered);
        registered.publishAfterCommit();


    }

    @PostUpdate
    public void onPostUpdate(){
        EnrollmentCanceled enrollmentCanceled = new EnrollmentCanceled();
        BeanUtils.copyProperties(this, enrollmentCanceled);
        enrollmentCanceled.publishAfterCommit();


    }


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
        this.confirmYn = confirmYn;
    }
    public String getConfirmYn() {
        return confirmYn;
    }

    public void setConfirmYn(String status) {
        this.confirmYn = confirmYn;
    }




}
