package kookmin.cs.capstone2.booking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mysql.jdbc.ResultSetMetaData;

import kookmin.cs.capstone2.GCM.GcmSender;
import kookmin.cs.capstone2.common.MyHttpServlet;
import kookmin.cs.capstone2.common.StaticVariables;
import kookmin.cs.capstone2.common.StaticMethods;

//관리자가 예약 신청 승인 또는 거절을 위해 내역을 받는다.

public class BookingRequest extends MyHttpServlet {

	/*
	 * request : 세미나실 id, 신청자 id, 날짜(yyyy-MM-dd), 시작시간, 끝시간, 예약 목적, 세미나 참석자 id
	 * response : 회원 등록 성공 여부
	 */

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		super.service(request, response);

		// RequestBody to String
		//String requestString = StaticMethods.getBody(request);

		// request 파라미터에서 json 파싱
		JSONObject requestJSON = (JSONObject) JSONValue.parse(requestString);
		System.out.println(requestJSON.toString());
		
		String roomId = requestJSON.get("roomId").toString();
		String userId = requestJSON.get("userId").toString();
		String date = requestJSON.get("date").toString();
		String startTime = requestJSON.get("startTime").toString();
		String endTime = requestJSON.get("endTime").toString();
		String context = requestJSON.get("context").toString();
		JSONArray participantsJSONArray = (JSONArray) requestJSON.get("participants");

		System.out.println(roomId + " " + userId + " " + date + " " + startTime
				+ " " + endTime + " " + context);

		PrintWriter pw = response.getWriter();
		
		int result = -1;

		try {
			conn = DriverManager.getConnection(StaticVariables.JOCL); // 커넥션 풀에서 대기 상태인 커넥션을 얻는다
			stmt = conn.createStatement(); // DB에 SQL문을 보내기 위한 Statement를 생성

			// 오토커밋을 false로 지정하여 트랜잭션 조건을 맞춘다
			conn.setAutoCommit(false);

			String sql;
			
			// 중복된 데이터가 있는지 확인하기
			sql = "select * from reservationinfo where "
					+ "date='" + date + "'" + 
					" and room_id=" + roomId + 
					" and ("+
					"(start_time < '" + startTime + "' and end_time > '" + startTime +"') or " +
					"(start_time > '" + startTime + "' and start_time < '" + endTime +"') or " +
					"(start_time <= '" + startTime + "' and end_time >='" + endTime + "') or " +
					"(start_time >= '" + startTime + "' and end_time <= '" + endTime + "'));";
			rs = stmt.executeQuery(sql);
			System.out.println("bookingrequest sql : " + sql + "\n\n");
			
			if(rs.next()){ // 중복이 있을 때
				result = StaticVariables.DUPLICATION;
			} else {
				sql = "insert into reservationinfo(room_id, user_id, date, start_time, end_time, context) "
						+ "values (" + roomId+ "," + userId+ ", '" + date + "', '" + startTime + "', '" + endTime+ "', '"+ context+ "');";
	
				// insert 후 생성된 고유 id값(key) 받아오기
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				rs = stmt.getGeneratedKeys();
				rs.next();
				int key = rs.getInt(1);
	
				if(participantsJSONArray.size() != 0){
					////////////////////////////////////////////////////////참석자 없을 때.
					// participantsArray의 회원들, table seminarmember에 insert하기
					sql = "insert into seminarmember(id, user_id) " + "values ";
					JSONObject participantObject = new JSONObject();
					String participantId = "";
					for (int i = 0; i < participantsJSONArray.size(); i++) {
						participantObject = (JSONObject) participantsJSONArray.get(i);
						participantId = participantObject.get("id").toString();
						sql += "(" + key + "," + participantId + ")";
						if ( i != participantsJSONArray.size() - 1 ) 
							sql += ", ";
					}
					System.out.println(sql);
					stmt.executeUpdate(sql);
				}
				
				conn.commit();
				result = StaticVariables.SUCCESS;
				// 예약 신청 완료 후 관리자에게 푸시 알람 전송
				GcmSender gs = new GcmSender();
				sql = "select reg_id from gcmid, user where gcmid.id=user.id and b_admin=1";
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
								gs.sendPush(rs.getString("reg_id"), "새로운 예약 신청");
				}
			}
		} catch (SQLException se) {
			System.out.println(se.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//responseJSONObj.put("status", StaticVariables.ERROR_MYSQL);
			result = StaticVariables.ERROR_MYSQL;
		} finally {
			//pw.println(responseJSONObj);
			pw.println(result);
			pw.close();
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}
}
