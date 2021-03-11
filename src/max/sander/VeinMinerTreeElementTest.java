package max.sander;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VeinMinerTreeElementTest {

    // this is a random mess currently
    // i copied a template and started editing it
    // realized what i'm doing might not help the situation

    private VeinMinerTreeElement currentElement;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public void setUp() {
        VeinMinerTreeElement root = new VeinMinerTreeElement(27, 14, 14);
        currentElement = new VeinMinerTreeElement(26, 14, 14, root);
        currentElement.setType("mined");
        VeinMinerTreeElement temp = new VeinMinerTreeElement(26, 14, 15, currentElement);
        new VeinMinerTreeElement(26, 14, 13, currentElement);
        new VeinMinerTreeElement(25, 14, 14, currentElement);
        currentElement = temp;
        currentElement.setType("mined");
        temp = new VeinMinerTreeElement(26, 14, 16, currentElement);
        new VeinMinerTreeElement(25, 14, 15, currentElement);
        currentElement = temp;
        currentElement.setType("mined");
        temp = new VeinMinerTreeElement(26, 14, 16, currentElement);
        new VeinMinerTreeElement(25, 14, 15, currentElement);
        currentElement = temp;
    }

    /**
     * Tears down the test fixture.
     * (Called after every test case method.)
     */
    @After
    public void tearDown() {
    }

    @Test
    public void testSomeBehavior() {
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testForException() {
    }

}