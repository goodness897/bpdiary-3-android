package kr.co.openit.bpdiary.common.helper;

import android.util.Log;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import kr.co.openit.bpdiary.common.constants.ManagerConstants.AppConfig;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.HTTPHeader;
import kr.co.openit.bpdiary.common.controller.ByteArrayMemoryBuffer;
import kr.co.openit.bpdiary.common.controller.IDownloadBuffer;

/**
 * 서비스 게이트 웨이 호출
 */
public final class HTTPHelper {

    protected static IDownloadBuffer mDownloadBuffer = new ByteArrayMemoryBuffer();

    /**
     * 조회
     *
     * @param serviceURI
     * @param requestMap
     * @param lang
     * @return
     */
    public static JSONObject doGet(String serviceURI, Map requestMap, String lang) {
        return connect("GET", AppConfig.SERVER_URL, serviceURI, parseParameter(requestMap), lang);
    }

    /**
     * 조회
     *
     * @param serviceURI
     * @param seq
     * @param lang
     * @return
     */
    public static JSONObject doGetForAds(String serviceURI, String seq, String lang) {
        return connect("GET", AppConfig.SERVER_URL, serviceURI, seq, lang);
    }

    /**
     * 등록
     *
     * @param serviceURI
     * @param requestJSON
     * @param lang
     * @return
     */
    public static JSONObject doPost(String serviceURI, JSONObject requestJSON, String lang) {
        return connect("POST", AppConfig.SERVER_URL, serviceURI, parseParameter(requestJSON), lang);
    }

    public static JSONObject doPost(String serviceURI, Map requestMap, String lang) {
        return connect("POST", AppConfig.SERVER_URL, serviceURI, parseParameter(requestMap), lang);
    }

    /**
     * 수정
     *
     * @param restHost
     * @param serviceURI
     * @param requestJSON
     * @param lang
     * @return
     */
    public static JSONObject doPut(String restHost, String serviceURI, JSONObject requestJSON, String lang) {
        return connect("PUT", restHost, serviceURI, parseParameter(requestJSON), lang);
    }

    /**
     * 삭제
     *
     * @param restHost
     * @param serviceURI
     * @param requestMap
     * @param lang
     * @return
     */
    public static JSONObject doDelete(String restHost, String serviceURI, Map requestMap, String lang) {
        return connect("DELETE", restHost, serviceURI, parseParameter(requestMap), lang);
    }

    /**
     * 파일 없이 데이터 전송
     *
     * @param method
     * @param restHost
     * @param serviceURI
     * @param parameter
     * @param lang
     * @return
     */
    public static JSONObject connect(String method, String restHost, String serviceURI, String parameter, String lang) {
        JSONObject response = null;
        OutputStream os = null;
        StringEntity entity;
        try {
            URL obj = new URL(restHost + serviceURI);
            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
            if ("GET".equals(method)) {
                conn.setRequestMethod("GET");
            } else if ("POST".equals(method)) {
                conn.setRequestMethod("POST");
            } else if ("PUT".equals(method)) {
                conn.setRequestMethod("PUT");
            } else if ("DELETE".equals(method)) {
                conn.setRequestMethod("DELETE");
            }
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty(HTTPHeader.ACCEPT_LANGUAGE, lang);
            conn.setRequestProperty(HTTPHeader.ACCEPT_ENCODING, "gzip");
            conn.setRequestProperty(HTTPHeader.CONTENT_TYPE, "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            os = conn.getOutputStream();
            entity = new StringEntity(parameter, "UTF-8");
            entity.writeTo(os);
            os.flush();

            String result = getResponse(conn);
            response = new JSONObject(result.toString());

            StringBuffer requestInfo = new StringBuffer();
            requestInfo.append("====================================================\n");
            requestInfo.append("METHOD : " + method + "\n");
            requestInfo.append("URI    : " + serviceURI + "\n");
            requestInfo.append("PARAMS : " + parameter + "\n");
            requestInfo.append("RESULT : " + response.toString() + "\n");
            requestInfo.append("====================================================\n");
            Log.i(HTTPHelper.class.getSimpleName(), requestInfo.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i(HTTPHelper.class.getSimpleName(), "TIMEOUT_EXCEPTION");
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.i(HTTPHelper.class.getSimpleName(), "THREAD_RUNTIME_EXCEPTION");
        } catch (SocketTimeoutException e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.i(HTTPHelper.class.getSimpleName(), "THREAD_RUNTIME_EXCEPTION");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(HTTPHelper.class.getSimpleName(), "HTTP_CONNECTION_EXCEPTION");
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }

        return response;
    }

    /**
     * 파라미터 유형을 분석하여 처리
     *
     * @param obj
     * @return
     */
    private static String parseParameter(Object obj) {
        StringBuilder sb = new StringBuilder();

        try {

            if (obj != null) {

                if (obj instanceof JSONObject) {
                    sb.append(obj.toString());
                } else {
                    Map requestMap = (Map) obj;

                    Iterator<String> itr = requestMap.keySet().iterator();

                    int paramSize = requestMap.size();
                    int cnt = 1;
                    while (itr.hasNext()) {
                        String key = itr.next();

                        sb.append(key).append("=").append(URLEncoder.encode((String) requestMap.get(key), "UTF-8"));

                        if (paramSize > cnt) {
                            sb.append("&");
                        }

                        cnt++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("parse parameter : " + sb.toString());

        return sb.toString();
    }

    /**
     * http 응답 받기
     *
     * @param conn 커넥션 상태
     * @return szResponse json 수신 데이터
     * @throws Exception
     */
    protected static String getResponse(HttpsURLConnection conn) throws Exception {
        InputStream is = null;
        InputStream isDummy = null;
        IDownloadBuffer buffer = mDownloadBuffer;
        try {
            is = conn.getInputStream();
            buffer.download(is);
            isDummy = buffer.getInputStream();
            String szResponse = readStringData(isDummy);
            return szResponse;
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (null != isDummy) {
                try {
                    isDummy.close();
                } catch (Exception e) {
                }
            }
            if (null != buffer) {
                buffer.closeInputStream();
            }
        }
    }

    /**
     * 서버로 부터 수신된 데이터 문자열을 모두 읽어 반환한다.
     *
     * @param in 데이터를 읽어들일 Input Stream
     * @return 수신된 데이터 정보
     * @throws Exception
     */
    protected static String readStringData(InputStream in) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder(1024 * 8);
        String szLine;
        while (null != (szLine = reader.readLine())) {
            sb.append(szLine);
        }
        return sb.toString();
    }
}