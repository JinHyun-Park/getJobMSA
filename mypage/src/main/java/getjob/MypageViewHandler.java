package getjob;

import getjob.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenApplied_then_CREATE_1 (@Payload Applied applied) {
        try {
            if (applied.isMe()) {
                // view 객체 생성
                 Mypage mypage = new Mypage();
                // view 객체에 이벤트의 Value 를 set 함
                mypage.setRecruitmentId(applied.getId());
                mypage.setJob(applied.getJob());
                mypage.setName(applied.getName());
                mypage.setAge(applied.getAge());
                mypage.setSkill(applied.getSkill());
                mypage.setStatus(applied.getStatus());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenConfirmed_then_UPDATE_1(@Payload Confirmed confirmed) {
        try {
            if (confirmed.isMe()) {
                // view 객체 조회
                List<Mypage> mypageList = mypageRepository.findByRecruitmentId(confirmed.getRecruitmentId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함

                    mypage.setConfirmYn(confirmed.getConfirmYn());
                    mypage.setJob(confirmed.getJob());
                    mypage.setStatus(confirmed.getStatus());

                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRegistered_then_UPDATE_2(@Payload Registered registered) {
        try {
            if (registered.isMe()) {
                // view 객체 조회
                List<Mypage> mypageList = mypageRepository.findByRecruitmentId(registered.getRecruitmentId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함

                    mypage.setEnrollmentId(registered.getId());
                    mypage.setQualificationId(registered.getQualificationId());
                    mypage.setConfirmYn(registered.getConfirmYn());
                    mypage.setJob(registered.getJob());
                    mypage.setStatus(registered.getStatus());

                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCanceled_then_UPDATE_3(@Payload Canceled canceled) {
        try {
            if (canceled.isMe()) {
                // view 객체 조회
                List<Mypage> mypageList = mypageRepository.findByRecruitmentId(canceled.getId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함

                    mypage.setStatus(canceled.getStatus());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenEnrollmentCanceled_then_UPDATE_4(@Payload EnrollmentCanceled enrollmentCanceled) {
        try {
            if (enrollmentCanceled.isMe()) {
                // view 객체 조회
                List<Mypage> mypageList =  mypageRepository.findByRecruitmentId(enrollmentCanceled.getRecruitmentId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함

                    mypage.setStatus(enrollmentCanceled.getStatus());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}