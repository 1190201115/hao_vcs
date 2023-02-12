package com.cyh.hao_vcs.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AutoCodeGenerator {
    private static Map<Integer,String> map = new HashMap<>();
    public static void main(String[] args)
    {
        initMap();
        Scanner sc = new Scanner(System.in);
        System.out.println("输入类名\n");
        String entityName = sc.next();
        writeEntity(entityName);
        writeMapper(entityName);
        writeService(entityName);

    }
    private static void initMap(){
        map.put(1,"String ");
        map.put(2,"Long ");
        map.put(3, "Integer ");
        map.put(4, "LocalDateTime ");
    }
    private static void writeEntity(String entityName){
        Scanner sc = new Scanner(System.in);
        String entityHead = "package com.cyh.hao_vcs.entity;\n\n" +
                "import lombok.Data;\n";
        String entityBody = "@Data\n";
        boolean addTime = false;
        entityBody += "public class "+ entityName +" {\n";
        System.out.println("输入参数个数\n");
        int num = sc.nextInt();
        int choice = 0;
        String paramName = null;
        while(num-- > 0){
            System.out.println("参数类型是？1.String 2.Long 3.Integer 4.LocalDateTime\n");
            choice = sc.nextInt();
            if(choice == 4 && !addTime){
                entityHead += "import java.time.LocalDateTime;\n";
                addTime = true;
            }
            System.out.println("参数名为？\n");
            paramName = sc.next();
            entityBody = entityBody + "private "+map.get(choice) + paramName + ";\n";
        }
        entityBody += "}";
        String filePath = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\java\\com\\cyh\\hao_vcs\\entity\\"+entityName+".java";
        String content = entityHead + entityBody;
        writeFile(filePath, content);
    }
    private static void writeMapper(String entityName){
        String content = "package com.cyh.hao_vcs.mapper;\n" +
                "\n" +
                "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
                "import org.apache.ibatis.annotations.Mapper;\n" +
                "import com.cyh.hao_vcs.entity."+entityName+";\n\n@Mapper\npublic interface "
                +entityName+"Mapper extends BaseMapper<"+entityName+"> {\n}";
        String filePath = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\java\\com\\cyh\\hao_vcs\\mapper\\"+entityName+"Mapper.java";
        writeFile(filePath,content);
    }
    private static void writeService(String entityName){
        String content = "package com.cyh.hao_vcs.service;\npublic interface "+entityName+"Service {\n}";
        String filePath = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\java\\com\\cyh\\hao_vcs\\service\\"+entityName+"Service.java";
        writeFile(filePath,content);
        writeServiceImpl(entityName);
    }

    private static void writeServiceImpl(String entityName){
        String content = "package com.cyh.hao_vcs.service.impl;\n" +
                "\n" +
                "import com.cyh.hao_vcs.service."+entityName+"Service;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "@Service\n" +
                "public class "+entityName+"ServiceImpl implements "+entityName+"Service {\n" +
                "}";
        String filePath = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\java\\com\\cyh\\hao_vcs\\service\\impl\\"+entityName+"ServiceImpl.java";
        writeFile(filePath,content);
    }
    private static void writeFile(String filePath,String content){
        FileWriter fw = null;
        try
        {
            File file = new File(filePath);
            file.createNewFile();
            fw = new FileWriter(filePath);
            fw.write(content);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fw.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
