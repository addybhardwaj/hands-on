import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
* TODO
*
* @author Aditya Bhardwaj
*/
public class CustomHashMapTest {

    @Test
    public void testPut() throws Exception {
        CustomHashMap map = new CustomHashMap(1);
        map.put("1", "1");
        assertEquals("1", map.get("1"));
        map.put("2", "2");
        assertEquals("1", map.get("1"));
        assertEquals("2", map.get("2"));

    }
}
