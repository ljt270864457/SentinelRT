package rt.sentinel.handler;

import rt.sentinel.core.SerializationContext;
import rt.sentinel.json.JsonBuilder;
import rt.sentinel.util.TypeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期类型处理器
 * 
 * 处理Date和Calendar类型，输出为ISO 8601格式字符串
 * 格式：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
 * 
 * @author SentinelRT
 */
public class DateHandler implements TypeHandler {
    
    /** ISO 8601 日期格式 */
    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    /** UTC时区 */
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    
    @Override
    public boolean canHandle(Class<?> clazz) {
        return TypeUtils.isDate(clazz);
    }
    
    @Override
    public void handle(Object obj, JsonBuilder builder, SerializationContext context) {
        if (obj == null) {
            builder.nullValue();
            return;
        }
        
        Date date;
        if (obj instanceof Date) {
            date = (Date) obj;
        } else if (obj instanceof Calendar) {
            date = ((Calendar) obj).getTime();
        } else {
            // 不应该到达这里
            builder.value(obj.toString());
            return;
        }
        
        // 格式化为ISO 8601
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        sdf.setTimeZone(UTC);
        builder.value(sdf.format(date));
    }
    
    @Override
    public int getPriority() {
        return 40;
    }
}
