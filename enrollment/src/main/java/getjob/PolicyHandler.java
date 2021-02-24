package getjob;

import getjob.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverConfirmed_(@Payload Confirmed confirmed){

        if(confirmed.isMe()){
            System.out.println("##### listener  : " + confirmed.toJson());

            Enrollment enroll = new Enrollment();

            enroll.setRecruitmentId(confirmed.getRecruitmentId());
            enroll.setConfirmYn("Y");
            enroll.setJob(confirmed.getJob());
            enroll.setStatus("You application is confirmed!");

            enrollmentRepository.save(enroll);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCanceled_(@Payload Canceled canceled){

        if(canceled.isMe()){
            System.out.println("##### listener  : " + canceled.toJson());

            Enrollment enroll = enrollmentRepository.findByRecruitmentId(canceled.getId());

            enroll.setStatus("Cancel!");
            enrollmentRepository.save(enroll);

        }
    }

}
