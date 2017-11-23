package gson;

import com.google.common.truth.Truth;
import com.google.gson.Gson;

import org.junit.Test;

/**
 * @author zeyu 2017/11/23
 */
public class GsonBaseTest {
    Gson gson = new Gson();

    /**
     * 当enum不存在时,反序列化出来是null
     * @throws Exception
     */
    @Test
    public void testEnum() throws Exception {
        String resStr = gson.toJson(SpiderResType.IMG, SpiderResType.class);
        Truth.assertThat(resStr).isEqualTo("\"IMG\"");
        SpiderResType resType = gson.fromJson("FILM",SpiderResType.class);
        Truth.assertThat(resType).isEqualTo(SpiderResType.FILM);
        resType = gson.fromJson("FILM1",SpiderResType.class);
        Truth.assertThat(resType).isNull();
    }

    public enum SpiderResType{
        IMG,FILM;
    }


}