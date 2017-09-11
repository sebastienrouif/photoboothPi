package uk.co.frips.photobooth;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= 188; i++) {
            stringBuilder.append(String.format("<image filename='http://as-app.com/photobooth/photobooth_%03d.jpg'></image>", i));
        }
        System.out.println(stringBuilder.toString());
    }
}