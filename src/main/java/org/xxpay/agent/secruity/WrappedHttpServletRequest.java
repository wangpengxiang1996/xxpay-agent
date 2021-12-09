//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.secruity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

public class WrappedHttpServletRequest extends HttpServletRequestWrapper {
    private byte[] bytes;
    private WrappedHttpServletRequest.WrappedServletInputStream wrappedServletInputStream;

    public WrappedHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.bytes = IOUtils.toByteArray(request.getInputStream());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.bytes);
        this.wrappedServletInputStream = new WrappedHttpServletRequest.WrappedServletInputStream(byteArrayInputStream);
        this.reWriteInputStream();
    }

    public void reWriteInputStream() {
        this.wrappedServletInputStream.setStream(new ByteArrayInputStream(this.bytes != null ? this.bytes : new byte[0]));
    }

    public ServletInputStream getInputStream() throws IOException {
        return this.wrappedServletInputStream;
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.wrappedServletInputStream));
    }

    public String getRequestParams() throws IOException {
        return new String(this.bytes, this.getCharacterEncoding());
    }

    private class WrappedServletInputStream extends ServletInputStream {
        private InputStream stream;

        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        public WrappedServletInputStream(InputStream stream) {
            this.stream = stream;
        }

        public int read() throws IOException {
            return this.stream.read();
        }

        public boolean isFinished() {
            return true;
        }

        public boolean isReady() {
            return true;
        }

        public void setReadListener(ReadListener readListener) {
        }
    }
}
