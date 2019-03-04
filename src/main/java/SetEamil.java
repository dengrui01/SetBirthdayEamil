import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * Created by dr on 2019/3/2.
 */
public class SetEamil {
    private static String account = "";
    private static String password = "";
    private final static String servicehost = "smtp.163.com";
    public static void main(String[] args) {
        String fileName = "employee_records.txt";
        account = "dengruiuse@163.com";
        password = "dr006860";
        ArrayList<String> list = readFile(fileName);
        cutList(list);

    }
    private static ArrayList<String> readFile(String fileName){
            ArrayList<String> list = new ArrayList<>();
            try {
                FileInputStream fi = new FileInputStream(fileName);
                InputStreamReader is = new InputStreamReader(fi,"GBK");
                BufferedReader bf = new BufferedReader(is);
                String str;

                while ((str = bf.readLine()) != null){
                    list.add(str);
                }

                fi.close();
                is.close();
                bf.close();
            }catch (IOException e){
                System.out.println("文件流读取错误");
           }

            return list;
        }
        //处理arraylist
    private static void cutList(ArrayList<String> list){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String curDate = df.format(new Date());
        if (list.size() != 0){
            for (int i = 0; i < list.size(); i++) {
                String[] line = list.get(i).split(", ");
                if (i == 0 && line[0].equals("last_name")){
                    //去除第一行
                        list.remove(i);
                        i--;
                }else {
                    //判断日期
                    if(line[2].equals(curDate)){
                        settingEamil(line[1],line[3]);
                    }
                }
            }
        }
    }

    private static void settingEamil(String name, String recAddr){
     try{
         Properties prop = new Properties();
         prop.setProperty("mail.transport.protocol", "smtp");
         prop.setProperty("mail.smtp.host", servicehost);
         prop.setProperty("mail.smtp.port",  "465");
         prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
         prop.setProperty("mail.smtp.socketFactory.fallback", "false");
         prop.setProperty("mail.smtp.socketFactory.port", "465");
         prop.setProperty("mail.smtp.auth", "true"); // 需要请求认证
         prop.setProperty("mail.smtp.ssl.enable", "true");// 开启ssl

         Session session = Session.getDefaultInstance(prop);
         session.setDebug(true);
         //创建邮件
         MimeMessage message = createEmail(session, recAddr, name);

         //解决垃圾邮件问题，抄送自己
         message.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(account));
         //获取传输通道
         Transport transport = session.getTransport();
         transport.connect(servicehost,account, password);
         //连接，并发送邮件
         transport.sendMessage(message, message.getAllRecipients());
         transport.close();
         }catch (Exception e){
             System.out.println("发送失败");
         }
     }

            // 根据会话创建邮件
    private static MimeMessage createEmail(Session session, String recAddr, String name) throws Exception {
    MimeMessage msg = new MimeMessage(session);
        // address邮件地址, personal邮件昵称, charset编码方式
        InternetAddress fromAddress = new InternetAddress(account,
                "", "utf-8");
        // 设置发送邮件方
            msg.setFrom(fromAddress);
            InternetAddress receiveAddress = new InternetAddress(
                recAddr, "", "utf-8");
        // 设置邮件接收方
            msg.setRecipient(Message.RecipientType.TO, receiveAddress);
        // 设置邮件标题
            msg.setSubject("Happy birthday!", "utf-8");
            msg.setText("Happy birthday, dear "+ name +"!", "utf-8");
        // 设置显示的发件时间
            msg.setSentDate(new Date());
        // 保存设置
            msg.saveChanges();
            return msg;
    }

}
