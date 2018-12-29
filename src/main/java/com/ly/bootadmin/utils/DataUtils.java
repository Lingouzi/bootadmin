package com.ly.bootadmin.utils;

import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;

/**
 * @author linyun
 * @date 2018/11/24 18:08
 */
@Component
public class DataUtils {

    private static MongoTemplate template;

    @Autowired
    public DataUtils(MongoTemplate template) {
        DataUtils.template = template;
    }

    /**
     * 通用更新属性, 如果有特别的属性需要单独写方法
     *
     * @param request
     * @param clazz
     * @return
     */
    public static Object updateById(HttpServletRequest request, Class clazz) {
        String id = request.getParameter("id");
        String field = request.getParameter("field");
        String value = request.getParameter("value");
        try {
            if (StringUtils.isEmpty(value)) {
                return JsonResp.fail("参数值错误");
            }
            value = URLDecoder.decode(value, "utf-8");

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            Update u = new Update();
            // 通过反射得到属性对应的类别,然后转换value
            Field[] fields = clazz.getDeclaredFields();
            boolean update = false;
            for (Field f : fields) {
                if (f.getName().equals(field)) {
                    String typeName = f.getGenericType().getTypeName();
                    if (typeName.contains("Long")) {
                        u.set(field, Long.parseLong(value.trim()));
                    } else if (typeName.contains("String")) {
                        u.set(field, value);
                    } else if (typeName.contains("Integer")) {
                        u.set(field, Integer.parseInt(value.trim()));
                    } else {
                        u.set(field, value);
                    }
                    update = true;
                    break;
                }
            }
            if (update) {
                UpdateResult ur = template.updateFirst(query, u, clazz);
                if (ur.getMatchedCount() != 1 && ur.getModifiedCount() != 1) {
                    return JsonResp.fail("更新失败");
                }
            } else {
                return JsonResp.fail("没有找到更新字段");
            }
        } catch (NumberFormatException | SecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return JsonResp.fail("发生错误");
        }
        return JsonResp.success();
    }
}
