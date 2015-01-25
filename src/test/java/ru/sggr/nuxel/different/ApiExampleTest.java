package ru.sggr.nuxel.different;


import org.junit.Test;
import ru.sggr.nuxel.Bean;
import ru.sggr.nuxel.BeanValidator;
import ru.sggr.nuxel.NuxelService;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Simple test with the only purpose - demonstrate library usage within java code
 */
@SuppressWarnings("unused")
public class ApiExampleTest {

    public static final String TEST_XLS = "test.xls";

    @Test
    public void straightForwardTest(){
        InputStream testRequestStream = getClass().getClassLoader().getResourceAsStream(TEST_XLS);
        List<Bean> result = NuxelService.getInstance(testRequestStream).extractBeans();
        for (Bean bean : result){
            System.out.println( bean.name() + " " + bean.sequence() + " " + bean.oe());
        }
    }

    @Test
    public void validatorSemanticTest(){
        InputStream testRequestStream = getClass().getClassLoader().getResourceAsStream(TEST_XLS);
        NuxelService service = NuxelService.getInstance(testRequestStream);
        NuxelService afterValidatorAttachment = service.addValidator(new BeanValidator() {
            @Override
            public String validate(Bean bean) {
                return "Here goes some error description";
            }
        });
        assertEquals(service, afterValidatorAttachment); //aghf, validators is a mutable peace of ... now
    }

}
