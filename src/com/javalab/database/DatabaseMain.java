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

public class DatabaseMain {

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

		// 2. 쿼리문 실행 메소드 호출
		System.out.println("2. 쿼리문 실행 메소드 호출");
		selectAllProduct();
		System.out.println();

		// 3. 특정 카테고리에 소속된 상품들만 조회하는 메소드
		System.out.println("3. 특정 카테고리에 소속된 상품들만 조회하는 메소드");
		String categoryName = "전자제품";
		selectProductsByCategory(categoryName);
		System.out.println();
		
		// 4. 가격이 25,000원 이상인 상품들의 이름과 가격을 조회하시오.
		System.out.println("4. 가격이 25,000원 이상인 상품들의 이름과 가격을 조회하시오.");
		int selePrice = 25000;
		selectProductGatherThan(selePrice);

		// 5. 카테고리별로 카테고리명과 가격의 합계금액을 조회하되 금액이 큰 순서로
		System.out.println("5. 카테고리별로 카테고리명과 가격의 합계금액을 조회하되 금액이 큰 순서로");
		selectProductGroupByCategory();

		// 6. 상품 추가 :: 카테고리:식료품 / 상품ID : 기존 번호 +1 상품명 :양배추 / 가격 : 2000 / 입고일 : 20220710
		System.out.println("6. 상품 추가 :: 카테고리:식료품 / 상품ID : 기존 번호 +1 상품명 :양배추 / 가격 : 2000 / 입고일 : 20220710");
		insertProduct();

		// 7. 상품 가격 변경(수정) 탱크로리 상품의 가격을 600000으로 수정
		System.out.println("7. 상품 가격 변경(수정) 탱크로리 상품의 가격을 600000으로 수정");
		updateProduct();
		// 8. 자원반납
		closeResource();

	} // main end
	// 8. 자원반납
	private static void closeResource() { // 자원반환
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("자원해제 ERR! : " + e.getMessage());
		}
	} // 자원 반환 end
	// 9. 오버로딩 자원반납
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
	
//7번===============================================================

	private static void updateProduct() {
		try {
			int productId = 12;
			int price = 600000;
			String sql = "update product set price = ? ";
			sql += " where product_id = ? ";
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, price);
			pstmt.setInt(2, productId);

			int resultNo = pstmt.executeUpdate();

			if (resultNo > 0) {
				System.out.println("수정 성공!!!");
			} else {
				System.out.println("수정 실패!!!");
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			//자원 해제 메소드 호출
			closeResource(pstmt,rs);
		}
	}

//6번===============================================================
	private static void insertProduct() {
		try {
			int PRODUCT_ID = 22;
			String PRODUCT_NAME = "양배추";
			int PRICE = 2000;
			int CATEGORY_ID = 5;
			String RECEIPT_DATE = "20220710";

			String sql = "insert into product (PRODUCT_ID,PRODUCT_NAME,PRICE,CATEGORY_ID,RECEIPT_DATE)";
			sql += "values (?,?,?,?, to_date(?,'YYYY/MM/DD'))";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, PRODUCT_ID);
			pstmt.setString(2, PRODUCT_NAME);
			pstmt.setInt(3, PRICE);
			pstmt.setInt(4, CATEGORY_ID);
			pstmt.setString(5, RECEIPT_DATE);

			int resultRows = pstmt.executeUpdate();
			System.out.println();

			if (resultRows > 0) {
				System.out.println("저장성공");
			} else {
				System.out.println("저장실패");
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			//자원 해제 메소드 호출
			closeResource(pstmt,rs);
		}
	}

//===============================================================
//5번===============================================================
	private static void selectProductGroupByCategory() {
		try {
			String sql = "select c.category_name, sum(p.price)";
			sql += " from category c, product p";
			sql += " where p.category_id = c.category_id";
			sql += " GROUP by c.category_name";
			sql += " order by sum(p.price) desc";
			pstmt = con.prepareStatement(sql);
			System.out.println("3. stmt 객체 생성 성공 : ");

			rs = pstmt.executeQuery();
			System.out.println();

			while (rs.next()) {
				System.out.println(rs.getString("category_name") + "\t" + rs.getInt("sum(p.price)"));
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());

		} finally {
			//자원 해제 메소드 호출
			closeResource(pstmt,rs);
		}

	}

// 4번===============================================================
	private static void selectProductGatherThan(int selePrice) {
		try {
			String sql = "select  p.PRODUCT_NAME, p.PRICE";
			sql += " from product p";
			sql += " where p.price >= ? ";
			sql += "order by p.price desc";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, selePrice);
			System.out.println("3. stmt 객체 생성 성공 : ");

			rs = pstmt.executeQuery();
			System.out.println();

			while (rs.next()) {
				System.out.println(rs.getString("PRODUCT_NAME") + "\t" + rs.getInt("PRICE"));
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());

		} finally {
			//자원 해제 메소드 호출
			closeResource(pstmt,rs);
		}

	}
//====================================================================

//3번===============================================================
	private static void selectProductsByCategory(String categoryName) { // 특정 카테고리에 소속된 상품들만 조회하는 메소드
		try {
			// 3. SQL 쿼리문
			String sql = "select p.PRODUCT_ID, p.PRODUCT_NAME, p.PRICE, p.RECEIPT_DATE, p.CATEGORY_ID, c.CATEGORY_NAME";
			sql += " from product p, category c";
			sql += " where p.category_id = c.category_id";
			sql += " and c.category_name = ? ";
			sql += " order by p.price desc";
			// 4. prepareStatement 객체 얻음
			// 커넥션 객체를 통해서 데이터베이스에 쿼리(SQL)를 실행해주는
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, categoryName);
			System.out.println("3. stmt 객체 생성 성공 : ");

			// 5. 쿼리 실행
			// - 조회된 결과가 ResultSet 객체에 담겨옴
			rs = pstmt.executeQuery();
			System.out.println();

			// 6. rs.next()의 의미 설명
			while (rs.next()) {
				System.out.println(rs.getString("category_id") + "\t" + rs.getString("category_name") + "\t"
						+ rs.getString("product_id") + "\t" + rs.getString("product_name") + "\t"
						+ rs.getString("price") + "\t" + rs.getString("receipt_date"));
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());

		} finally {
			//자원 해제 메소드 호출
			closeResource(pstmt,rs);
		}

	}
//====================================================================

//2번===============================================================
	private static void selectAllProduct() {
		try {
			// 3. 쿼리문
			String sql = "select c.category_id, c.category_name, p.product_id, p.product_name,";
			sql += " p.price, to_char(receipt_date, 'yyyy-mm-dd') as receipt_date";
			sql += " from category c left outer join product p on c.category_id = p.category_id";
			sql += " order by c.category_id, p.product_id desc";

			// 4. prepareStatement 객체 얻음
			// 커넥션 객체를 통해서 데이터베이스에 쿼리(SQL)를 실행해주는
			pstmt = con.prepareStatement(sql);
			System.out.println("3. stmt 객체 생성 성공 : ");

			// 5. 쿼리 실행
			// - 조회된 결과가 ResultSet 객체에 담겨옴
			rs = pstmt.executeQuery();
			System.out.println();

			// 6. rs.next()의 의미 설명
			while (rs.next()) {
				System.out.println(rs.getString("category_id") + "\t" + rs.getString("category_name") + "\t"
						+ rs.getString("product_id") + "\t" + rs.getString("product_name") + "\t"
						+ rs.getString("price") + "\t" + rs.getString("receipt_date"));
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());

		} finally {
			//자원 해제 메소드 호출
			closeResource(pstmt,rs);
		}

	}
//====================================================================

// 1번===============================================================
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
	} // connectDB end

//====================================================================

}