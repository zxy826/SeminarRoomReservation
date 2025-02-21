package kookmin.cs.capstone2.room;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import sun.rmi.runtime.Log;
import kookmin.cs.capstone2.common.MyHttpServlet;
import kookmin.cs.capstone2.common.StaticMethods;
import kookmin.cs.capstone2.common.StaticVariables;

/*
 * filename : DoorControl.java
 * 기능 : 관리자가 요청할 때 세미나실 문을 열고 닫는다.
 */
public class DoorControl extends MyHttpServlet {

	String command = null;

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		super.service(request, response);

		//RequestBody to String
		//String requestString = StaticMethods.getBody(request);
		//System.out.println(requestString);
				
		// request 파라미터로 전송된 값 얻기 => !!json으로 수정해야 함
		// ******************************* //송미랑도 맞춰야함
		String userId = request.getParameter("id"); // 사용자 고유 id
		String roomStr = request.getParameter("roomName");
		command = request.getParameter("command");

		System.out.println("doorControl : " + userId + " " + roomStr + " " + command);

		PrintWriter pw = response.getWriter();

		try {
			conn = DriverManager.getConnection(StaticVariables.JOCL); // 커넥션 풀에서 대기상태인 커넥션을 얻는다
			stmt = conn.createStatement(); // DB에 SQL문을 보내기 위한 Statement를 생성

			conn.setAutoCommit(false);// 오토커밋을 false로 지정하여 트랜잭션 조건을 맞춘다

			String sql = // 방 잠금 장치 상태 변경 명령
			"update room set status=" + command + " where id=" + roomStr + ";";
			
			int updateResult = stmt.executeUpdate(sql);// return the row count for SQL DML statements

			if (updateResult == 1) { // update 성공했을 경우

				// 출입 기록 삽입
				sql = "insert into roomhistory (room_id, user_id, command) values "
						+ "(" + roomStr + ", " + userId + ", " + command + ");";
				int insertResult = stmt.executeUpdate(sql); // roomhistory에 기록 추가

				if (insertResult == 1) {
					int result = StaticMethods.rasberrySocket(command, roomStr); // 라즈베리파이에 요청 보내기
					if (result == StaticVariables.SUCCESS) {
						responseJsonObj.put("result", StaticVariables.SUCCESS);
						conn.commit();
					} else {
						responseJsonObj.put("result", StaticVariables.FAIL);
						conn.rollback();
					}
				}
			}
		} catch (SQLException e) { 
			System.err.print("DoorControl SQLException: ");
			System.err.println(e.getMessage());
			e.printStackTrace();
			if (conn != null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			responseJsonObj.put("result", StaticVariables.ERROR_MYSQL);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage()); // 주로 커넥션 문제
				responseJsonObj.put("result", StaticVariables.ERROR_MYSQL);
			}
			pw.println(responseJsonObj);
		}
	}
}
