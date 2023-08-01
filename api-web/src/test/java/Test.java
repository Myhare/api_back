import com.ming.web.WebApplication;
import com.ming.web.service.scheduled.CheckOnlineInterface;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = WebApplication.class)
class WebApplicationTest {

    @Resource
    private CheckOnlineInterface checkOnlineInterface;

    @Test
    void test(){
        checkOnlineInterface.checkOnlineInterface();
    }

}
