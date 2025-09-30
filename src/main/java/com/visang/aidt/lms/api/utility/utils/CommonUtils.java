package com.visang.aidt.lms.api.utility.utils;

import java.security.SecureRandom;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 공통 유틸리티 클래스
 */
public final class CommonUtils {

	private CommonUtils() {
	}

	/** 업로드 허용 파일 형식(content-type 기준) **/
	private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
			"image/jpeg", "image/jpg", "image/gif", "image/bmp", "image/png", "image/webp",
			"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
	));

	/** 업로드 허용 파일 확장자 **/
	private static final Set<String> ALLOWED_CONTENT_EXTS = new HashSet<>(Arrays.asList(
			"jpeg", "jpg", "gif", "bmp", "png", "webp",
			"xls", "xlsx"
	));


	// ****************************************************************************************************************
	// 시간 관련 유틸리티
	// ****************************************************************************************************************
	/**
	 * 시간 비교헤서 시간 차이 값 반환
	 * @param timestamp
	 * @return
	 */
    public static int diffDatetime(String timestamp) {
   		Calendar now = Calendar.getInstance();
		int yy = 0, mm = 0, dd = 0, hh = 0, mi = 0;

		int diff = 0;

		// 자릿수 확인 및 시간 비교
		if (timestamp.length() == 14) {
			yy = Integer.parseInt(timestamp.substring(0, 4));
			mm = Integer.parseInt(timestamp.substring(4, 6));
			dd = Integer.parseInt(timestamp.substring(6, 8));
			hh = Integer.parseInt(timestamp.substring(8, 10));
			mi = Integer.parseInt(timestamp.substring(10, 12));

			Calendar cts = new GregorianCalendar(yy, mm - 1, dd, hh, mi);

			diff = (int) ((now.getTimeInMillis() - cts.getTimeInMillis()) / (60 * 1000));
		} else {
			diff = -1;
		}

		return diff;
   	}

	/**
	 * 현재 시간 반환
	 * @return
	 */
	public static String currentTimeStamp() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date time = new Date();
		return format.format(time);
	}




	// ****************************************************************************************************************
	// 파일 관련 유틸리티
	// ****************************************************************************************************************
	/**
	 * 임의의 파일명 생성
	 * @param originalFileName
	 * @return
	 */
	public static String generateFileName(String originalFileName) {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

		return uuid + "." + extension;
	}

	/**
	 * 파일 사이즈 반환
	 * @param maxSize
	 * @return
	 */
	public static long parseMaxFileSize(String maxSize) {
		String lowerCaseMaxSize = maxSize.toLowerCase();

		if (lowerCaseMaxSize.endsWith("kb")) {
			return parseSizeValue(lowerCaseMaxSize, 1024);
		} else if (lowerCaseMaxSize.endsWith("mb")) {
			return parseSizeValue(lowerCaseMaxSize, 1024 * 1024);
		} else if (lowerCaseMaxSize.endsWith("gb")) {
			return parseSizeValue(lowerCaseMaxSize, 1024 * 1024 * 1024);
		} else {
			return Long.parseLong(lowerCaseMaxSize);
		}
	}

	/**
	 * 문자열로 전달된 파일 사이즈를 계산하여 반환 (KB, MB, GB)
	 * @param sizeString
	 * @param multiplier
	 * @return
	 */
	private static long parseSizeValue(String sizeString, long multiplier) {
		return Long.parseLong(sizeString.substring(0, sizeString.length() - 2)) * multiplier;
	}

	/**
	 * 파일 Content-Type 확인하여 허용여부 반환
	 * @param contentType
	 * @return
	 */
	public static boolean isAllowedFileType(String contentType) {
		// 허용된 Content-Type인지 확인
		return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType);
	}

	/**
	 * 허용된 파일 Content-Type 반환
	 * @return
	 */
	public static String getAllowedFileTypes() {
		StringBuilder sb = new StringBuilder();
		for (String contentType : ALLOWED_CONTENT_TYPES) {
			sb.append(contentType).append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}

	/**
	 * 파일 확장자 반환
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null || fileName.lastIndexOf(".") == -1) {
			return "";
		}

		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	/**
	 * 허용된 파일 확장자인지 확인
	 * @param fileName
	 * @return
	 */
	public static boolean isAllowedFileExts(String fileName) {
		String exts = getFileExtension(fileName);

		return ALLOWED_CONTENT_EXTS.contains(exts);
	}

	/**
	 * 허용된 파일 확장자 반환
	 * @return
	 */
	public static String getAllowedFileExts() {
		StringBuilder sb = new StringBuilder();
		for (String exts : ALLOWED_CONTENT_EXTS) {
			sb.append(exts).append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}

	/**
	 * 경로 조작 공격 방지를 위한 경로 검증
	 *
	 * @param filePath 검증할 경로
	 * @return true: 허용된 경로, false: 허용되지 않은 경로
	 */
	public static boolean validateFilePath(String filePath) {
		if (filePath == null || filePath.isEmpty()) {
			return false;
		}
		return filePath.matches("[\\w\\-\\./\\\\]*");
	}

	/**
	 * 경로 조작 공격 방지를 위한 파일명 검증
	 *
	 * @param fileName 검증할 파일명
	 * @return true: 허용된 파일명, false: 허용되지 않은 파일명
	 */
	public static boolean validateFileName(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return false;
		}
		return fileName.matches("[\\w\\-\\.가-힣\\s]*");
	}



	// ****************************************************************************************************************
	// Spring Bean 관련 유틸리티
	// ****************************************************************************************************************
	/**
	 * 빈 값인 프로퍼티 반환
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for(java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}


	// ****************************************************************************************************************
	// 암호화 관련 유틸리티
	// ****************************************************************************************************************
	public static String encryptString(String input) {
		try {
			// SHA-256 알고리즘 사용
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte[] salt = newSalt();
			md.update(salt);

			// 입력 문자열의 바이트 배열로 해시 계산
			byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

			// 바이트 배열을 부호 없는 정수로 변환
			BigInteger no = new BigInteger(1, messageDigest);

			// 해시 값을 16진수로 변환
			String hashtext = no.toString(16);

			// 32비트 해시 값을 만들기 위해 0을 추가
			while (hashtext.length() < 64) {  // SHA-256 해시는 64자입니다.
				hashtext = "0" + hashtext;
			}

			// 해시 값을 반환
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String encryptSaltString(String keySaltPrefix, String keySaltSuffix, String input) {
		try {
			// SHA-256 알고리즘 사용
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			input = keySaltPrefix + input + keySaltSuffix;

			// 입력 문자열의 바이트 배열로 해시 계산
			byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

			// 바이트 배열을 부호 없는 정수로 변환
			BigInteger no = new BigInteger(1, messageDigest);

			// 해시 값을 16진수로 변환
			String hashtext = no.toString(16);

			// 32비트 해시 값을 만들기 위해 0을 추가
			while (hashtext.length() < 64) {  // SHA-256 해시는 64자입니다.
				hashtext = "0" + hashtext;
			}

			// 해시 값을 반환
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] newSalt() {
		byte[] salt = new byte[16];
		new SecureRandom().nextBytes(salt);
		return salt;
	}



}
