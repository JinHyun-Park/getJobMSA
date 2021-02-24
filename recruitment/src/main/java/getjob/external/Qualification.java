package getjob.external;

public class Qualification {

    private Long id;
    private Long recruitmentId;
    private String confirmYn;
    private String status;
    private String job;

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
