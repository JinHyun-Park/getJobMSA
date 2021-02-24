
package getjob.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="qualification", url="${api.qualification.url}")
public interface QualificationService {

    @RequestMapping(method= RequestMethod.POST, path="/qualifications")
    public void check(@RequestBody Qualification qualification);

}