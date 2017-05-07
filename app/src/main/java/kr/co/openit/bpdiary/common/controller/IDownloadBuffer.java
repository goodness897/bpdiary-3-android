package kr.co.openit.bpdiary.common.controller;

import java.io.IOException;
import java.io.InputStream;

/**
 * 서버로 부터 다운로드된 데이터 처리를 위해 임시로 저장할 구현부 구현을 위한 추상 클래스.</br>
 * 요구 사항에 따라 메모리/내부 저장소/외부 저장소에 저장하는 클래스를 생성하여 사용한다.</br>
 */
public interface IDownloadBuffer {
	/**
	 * stream 으로 부터 데이터를 받아온다.
	 * @param stream 데이터를 읽을 inputstream
	 * @return 받은 데이터 개수
	 * @throws IOException
	 */
	public int download(InputStream stream) throws IOException;
	/**
	 * {@link #download(InputStream) download} 로 부터 받은 데이터를 접근하기 위핸 InputStream
	 * @return Download 된 데이터를 읽기 위한 InputStream
	 */
	public InputStream getInputStream();
	/**
	 * {@link getInputStream())을 닫는다. 필요에 따라 자원을 해제한다.</br>
	 * 파일로 구현된 경우 파일을 삭제하고, 메모리로 구현된 경우 할당된 메모리를 해제한다.
	 */
	public void closeInputStream();
} // End of interface IMemoryBuffer
