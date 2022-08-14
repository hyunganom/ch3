import com.fastcampus.ch3.User;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/**/root-context.xml"})
public class DBconnectionTest2Test extends TestCase {
    @Autowired
    DataSource ds;

    @Test
    public void insertUserTest() throws Exception {
        User user = new User("asdf3", "1234", "abc", "aaa.@casd.com", new Date(), "facebook", new Date());
        deletAll();
        int rowCnt = inserUser(user);

        assertTrue(rowCnt == 1);
    }


    @Test
    public void selectUserTest() throws Exception{
        deletAll();
        User user = new User("asdf", "1234", "abc", "aaa.@casd.com", new Date(), "facebook", new Date());
        int rowCnt = inserUser(user);
        User user2 = selectUser("asdf");

        assertTrue(user.getId().equals("asdf"));
    }

    public int deleteUser(String id) throws Exception{
        Connection conn = ds.getConnection();

        String sql = "delete * from user_info where id= ? ";
        PreparedStatement pstmt = conn.prepareStatement(sql); //SQL Injection 공격, 성능향샹
        pstmt.setString(1, id);
        int rowCnt = pstmt.executeUpdate(); // insert, update, delete때 씀
        return rowCnt;
    }

    public User selectUser(String id) throws Exception {
        Connection conn = ds.getConnection();

        String sql = "select * from user_info where id= ? ";

        PreparedStatement pstmt = conn.prepareStatement(sql); // SQL Injection공격, 성능향상
        pstmt.setString(1,id);
        ResultSet rs = pstmt.executeQuery(); //  select

        if(rs.next()) {
            User user = new User();
            user.setId(rs.getString(1));
            user.setPwd(rs.getString(2));
            user.setName(rs.getString(3));
            user.setEmail(rs.getString(4));
            user.setBirth(new Date(rs.getDate(5).getTime()));
            user.setSns(rs.getString(6));
            user.setReg_date(new Date(rs.getTimestamp(7).getTime()));
            return user;
        }
        return null;
    }

    private void deletAll() throws Exception {
        Connection conn = ds.getConnection();

        String sql = "delete from user_info";

        PreparedStatement pstmt = conn.prepareStatement(sql); //SQL Injection 공격, 성능향샹
        pstmt.executeUpdate(); // insert, update, delete때 씀

    }

    //사용자 정보를 user_info테이블에 저장하는 메서드
    public int inserUser(User user) throws Exception {
        Connection conn = ds.getConnection();

//        insert into user_info (id, pwd, name, email, birth, sms, reg_date)
//        values ('aqws2','1234','바보','222@2222.com','2021-08-14','facebook',now());

        String sql = "insert into user_info values (?,?,?,?,?,?,now())";

        PreparedStatement pstmt = conn.prepareStatement(sql); //SQL Injection 공격, 성능향샹
        pstmt.setString(1, user.getId());
        pstmt.setString(2, user.getPwd());
        pstmt.setString(3, user.getName());
        pstmt.setString(4, user.getEmail());
        pstmt.setDate(5, new java.sql.Date(user.getBirth().getTime()));
        pstmt.setString(6, user.getSns());

        int rowCnt = pstmt.executeUpdate(); // insert, update, delete때 씀


        return rowCnt;
    }

    @Test
    public void springJdbcConnectionTest() throws Exception {
//        ApplicationContext ac = new GenericXmlApplicationContext("file:src/main/webapp/WEB-INF/spring/**/root-context.xml");
//        DataSource ds = ac.getBean(DataSource.class);

        Connection conn = ds.getConnection(); // 데이터베이스의 연결을 얻는다.

        System.out.println("conn = " + conn);
        assertTrue(conn != null); // 괄호 안의 조건식이 true면, 테스트 성공, 아니면 실패
    }
}