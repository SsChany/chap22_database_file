package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * [static 전역변수]
 * JDBC 프로그래밍을 위한 요소들을 모두 멤버변수 즉, 필드 위치로 뽑아올림.
 *  - 본 클래스 어디서라도 사용가능한 전역변수가 됨.
 *  [모듈화]
 *  - 데이터베이스 커넥션 + PreparedStatement + 쿼리실행 작업 모듈
 *  - 실제로 쿼리를 실행하고 결과를 받아오는 부분 모듈
 *  [미션]
 *  - 전체 상품의 정보를 조회하세요(카테고리명이 나오도록)
 */

public class PointMain {

	// [멤버 변수]
	// 1. oracle 드라이버 이름 문자열 상수
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	// 2. oracle 데이터베이스 접속 경로(url) 문자열 상수
	public static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";

	// 3. 데이터베이스 접속 객체
	public static Connection con = null;
	// 4. query 실행 객체
	public static PreparedStatement pstmt = null;
	// 5. select 결과 저장 객체
	public static ResultSet rs = null;

	// 6. oracle 계정(id/pwd)
	public static String oracleId = "tempdb";
	// 7. oracle Password
	public static String oraclePwd = "1234";

	// main 메소드
	public static void main(String[] args) {

		// 1. 디비 접속 메소드 호출
		System.out.println("1. 디비 접속 메소드 호출");
		connectDB();
		
		System.out.println("2. 회원, 포인트 정보 조회");
		getMemberAndPoint();
		
		System.out.println("3. 이소미 회원에게 포인트 15점 추가 지급");
		updatePointSomi();
		
		System.out.println("4. 관리자에게 포인트 30점 추가 지급");
		updatePointManager();
		
		System.out.println("5. 전체 회원 평균 포인트보다 작은 회원 목록 조회");
		getMembersLessThanAvg();
		
		System.out.println("6. Connection 자원 반환");
		closeResource();	//모든 리소스를 닫음.

	} // main end

	// 6. Connection 자원 반환
	private static void closeResource() { // 자원반환
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("자원해제 ERR! : " + e.getMessage());
		}
	} // 자원 반환 end
	// 7. 오버로딩 자원반납
	private static void closeResource(PreparedStatement pstmt, ResultSet rs) { // 자원반환
		try {
			
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			System.out.println("자원해제 ERR! : " + e.getMessage());
		}
	} // 자원 반환 end
	//5. 전체 회원 평균 포인트보다 작은 회원 목록 조회
	private static void getMembersLessThanAvg() {
		try {
			String sql = "select  m.USER_ID, m.NAME, m.PWD, m.EMAIL, m.PHONE, decode(m.ADMIN, 0, '일반사용자',1,'관리자') admin, p.POINTS, to_char(p.reg_date,'yyyy-mm-dd') reg_date";
				  sql += " from member m left OUTER join point p on m.user_id = p.user_id";	
				  sql += " where p.points < (select avg(p.points)";	
				  sql += " from point p)";	
			
				  pstmt = con.prepareStatement(sql);
				  System.out.println("pstmt 객체 생성 성공 : ");
				  
				  rs = pstmt.executeQuery();
			
				  System.out.println("USER_ID "
						  	 +"\t"+ " NAME " 
						  	 +"\t"+ " PWD " 
						  	 +"\t"+ " EMAIL " 
						  	 +"\t"+ " PHONE " 
						  	 +"\t"+ " admin " 
						  	 +"\t"+ " POINTS " 
						  	 +"\t"+ " reg_date " 
						  );
				  
				  while(rs.next()) {
			            System.out.println(
			                  rs.getString("USER_ID") + "\t" 
			                  + rs.getString("NAME") + "\t"
			                  + rs.getString("PWD") + "\t"
			                  + rs.getString("EMAIL") + "\t"
			                  + rs.getString("PHONE") + "\t"
			                  + rs.getString("admin") + "\t" 
			                  + rs.getInt("POINTS") + "\t"
			                  + rs.getString("reg_date") + "\t"
			            );
			         }
		System.out.println("===========================================================");
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			closeResource(pstmt,rs);
		}
		System.out.println();
		}
	
	//4. 관리자에게 포인트 30점 추가 지급
	private static void updatePointManager() {
		String sql = "";
		try {
			// 수정할 회원 및 포인트 변수 선언
			int intPoint = 30;
			int strInt = 1;
			
			sql ="update point";
			sql +=" set points = points + ?";
			sql +=" where user_id in (select user_id";
			sql +=" from member";
			sql +=" where admin = ?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, intPoint);
			pstmt.setInt(2, strInt);
			
			int resultRows = pstmt.executeUpdate();
			
			if (resultRows > 0) {
				System.out.println("수정 성공");
			}else {
				System.out.println("수정 실패");
			}
			
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			closeResource(pstmt,rs);
		}
		System.out.println();
	}
	
	//3. 이소미 회원에게 포인트 15점 추가 지급
	private static void updatePointSomi() {
		String sql = "";
		try {
			// 수정할 회원 및 포인트 변수 선언
			int intPoint = 15;
			String strName = "이소미";
			
			sql ="update point";
			sql +=" set points = points + ?";
			sql +=" where user_id = (select USER_ID";
			sql +=" from member";
			sql +=" where name = ?)"; 
			
			 
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, intPoint);
			pstmt.setString(2, strName);
			
			int resultRows = pstmt.executeUpdate();
			
			if (resultRows > 0) {
				System.out.println("수정 성공");
			}else {
				System.out.println("수정 실패");
			}
			
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			closeResource(pstmt,rs);
		}
		System.out.println();
	}
	
	//2. 회원, 포인트 정보 조회
	private static void getMemberAndPoint() {
	try {
		String sql = "select m.USER_ID, m.NAME, m.PWD, m.EMAIL, m.PHONE, decode(m.ADMIN, 0, '일반사용자',1,'관리자') admin, p.POINTS, to_char(p.reg_date,'yyyy-mm-dd') reg_date";
			  sql += " from member m, point p";	
			  sql += " where m.user_id = p.user_id";	
		
			  pstmt = con.prepareStatement(sql);
			  System.out.println("pstmt 객체 생성 성공 : ");
			  
			  rs = pstmt.executeQuery();
		
			  System.out.println("USER_ID "
					  	 +"\t"+ " NAME " 
					  	 +"\t"+ " PWD " 
					  	 +"\t"+ " EMAIL " 
					  	 +"\t"+ " PHONE " 
					  	 +"\t"+ " admin " 
					  	 +"\t"+ " POINTS " 
					  	 +"\t"+ " reg_date " 
					  );
			  
			  while(rs.next()) {
		            System.out.println(
		            		rs.getString("USER_ID") + "\t" 
		                  + rs.getString("NAME") + "\t"
		                  + rs.getString("PWD") + "\t"
		                  + rs.getString("EMAIL") + "\t"
		                  + rs.getString("PHONE") + "\t"
		                  + rs.getString("admin") + "\t" 
		                  + rs.getInt("POINTS") + "\t"
		                  + rs.getString("reg_date") + "\t"
		            );
		         }
	} catch (SQLException e) {
		System.out.println("SQL ERR! : " + e.getMessage());
	} finally {
		closeResource(pstmt,rs);
	}
	System.out.println();
	}
	
// 1. 디비 접속 메소드 호출
	private static void connectDB() {
		try {
			// 1. 드라이버 로딩
			Class.forName(DRIVER_NAME);
			System.out.println("1. 드라이버 로드 성공!");

			// 2. 데이터베이스 커넥션(연결)
			con = DriverManager.getConnection(DB_URL, oracleId, oraclePwd);
			System.out.println("2. 커넥션 객체 생성 성공!");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 ERR! : " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}
		System.out.println();
	} // connectDB end

//====================================================================

}