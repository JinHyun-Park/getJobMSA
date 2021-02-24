package getjob;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Qualification_table")
public class Qualification {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long recruitmentId;
    private String confirmYn;
    private String status;
    private String job;

    @PostPersist
    public void onPostPersist(){
        Confirmed confirmed = new Confirmed();
        BeanUtils.copyProperties(this, confirmed);
        confirmed.publishAfterCommit();

        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


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
    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }




}
