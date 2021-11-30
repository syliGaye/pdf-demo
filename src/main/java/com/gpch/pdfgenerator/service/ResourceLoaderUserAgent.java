package com.gpch.pdfgenerator.service;

import com.lowagie.text.pdf.codec.Base64;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.util.XRLog;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class ResourceLoaderUserAgent extends ITextUserAgent {

    public ResourceLoaderUserAgent(ITextOutputDevice outputDevice) {
        super(outputDevice);
    }

    public InputStream resolveAndOpenStream(String uri) {

        FileURLConnection connection = null;
        URL proxyUrl = null;
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 9883));
            proxyUrl = new URL(uri);
            connection = (FileURLConnection) proxyUrl.openConnection(proxy);
            connection.connect();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        java.io.InputStream is = null;
        try {
            is = connection.getInputStream();
        } catch (java.net.MalformedURLException e) {
            XRLog.exception("bad URL given: " + uri, e);
        } catch (java.io.FileNotFoundException e) {
            XRLog.exception("item at URI " + uri + " not found");
        } catch (java.io.IOException e) {
            XRLog.exception("IO problem for " + uri, e);
        }

        return is;
    }
}