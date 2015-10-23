import org.junit.Test;

import static org.junit.Assert.*;


public class PersonTest {

    @Test
    public void testSayHello() throws Exception {
        assertEquals("Hello", new Person().sayHello());
    }

    @Test
    public void testSayBye(){
        assertEquals("Byeee", new Person().sayBye());
    }

}
