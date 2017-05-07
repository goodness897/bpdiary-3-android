package kr.co.openit.bpdiary.common.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 서버로 부터 데이터를 받아 ByteArrayInputStream에 저장한다.<br/>
 * 받은 데이터는 사용하는 곳에 제공한다. <br/>
 */
public final class ByteArrayMemoryBuffer implements IDownloadBuffer {
    /**
     * Web Resource 가 적재될 파일 스트림
     */
    protected ByteArrayInputStream mInFile;
    /**
     * Web resource URL
     */
    ///////////////////////////////////////////////////////////////////////////
    public ByteArrayMemoryBuffer() {
    	mInFile = null;
    } // End of Constructor
    ///////////////////////////////////////////////////////////////////////////
    public ByteArrayMemoryBuffer(InputStream stream) throws IOException {
        mInFile = null;
        download(stream);
    } // End of Constructor
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 서버의 파일로부터 가져온 데이터를 읽어 Memory에 쓰는 동작을 한다
     * @param stream 네트워크로 연결된 input stream
     * @return	입력 받은 데이터 수
     */
    @Override
    public int download(InputStream stream) throws IOException {
        int iCopy = 0;
        int iRead;
        byte[] buffer = new byte[1024];

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while (0 < (iRead = stream.read(buffer))) {
            out.write(buffer, 0, iRead);
            iCopy += iRead;
        } // End of while

        mInFile = new ByteArrayInputStream(out.toByteArray());

        return iCopy;
    } // End of copyFile
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 서버로 부터 받은 데이터를 가지고 있는 Input Stream을 반환한다.
     * @return InputStream
     */
    @Override
    public InputStream getInputStream() {
        return mInFile;
    } // End of getInputStream
    ///////////////////////////////////////////////////////////////////////////
    /**
     * ByteArrayInputStream을 닫는다.
     */
    @Override
    public void closeInputStream() {
        if (null != mInFile) {
            try {
                mInFile.close();
            } catch (Exception e) {
            } // End of catch
            mInFile = null;
        } // End of if
    } // End of closeInputStream
    ///////////////////////////////////////////////////////////////////////////
    
} // End of class MemoryBuffer
