import org.junit.Test;

import static org.junit.Assert.*;


public class PersonTest {

    @Test
    public void testSayHello() throws Exception {
        assertEquals("Hello", new Person().sayHello());
    }

    @Test
    public void testSayBye(){
        assertEquals("Bye", new Person().sayBye());
    }

    @Test
    public void testFailBye() {
        assertEquals("", new Person().sayBye());
    }
}