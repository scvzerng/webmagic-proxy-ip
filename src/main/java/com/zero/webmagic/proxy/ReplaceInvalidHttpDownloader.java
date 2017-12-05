package com.zero.webmagic.proxy;

import com.zero.webmagic.exception.ErrorPageException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.*;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.CharsetUtils;
import us.codecraft.webmagic.utils.HttpClientUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/3-14:42
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Component
@Slf4j
public class ReplaceInvalidHttpDownloader extends HttpClientDownloader {
    @Resource
    ProxyProvider proxyProvider;
    @PostConstruct
    public void init(){
        this.setProxyProvider(proxyProvider);
    }

    @Setter
    private Spider spider;



    @Override
    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
        Page page = super.handleResponse(request, charset, httpResponse, task);
        if(isValidPage().test(page)) throw new ErrorPageException("不是有效页面:"+page.getRawText());

        return page;
    }

    private Predicate<Page> isValidPage(){
        return page -> page.getStatusCode()!=200||!page.getHtml().get().contains("代理");
    }

    @Override
    protected void onError(Request request) {
        spider.addRequest(request);
    }
}
