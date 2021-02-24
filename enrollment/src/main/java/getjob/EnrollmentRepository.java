package getjob;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface EnrollmentRepository extends PagingAndSortingRepository<Enrollment, Long>{

    Enrollment findByRecruitmentId(Long recruitmentId);

}