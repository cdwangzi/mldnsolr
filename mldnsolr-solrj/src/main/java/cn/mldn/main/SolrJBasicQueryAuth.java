package cn.mldn.main;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrJBasicQueryAuth {
	// 定义要操作的Solr-Core的访问路径，如果存在有认证则需要配置有认证的信息
	public static final String SOLR_HOST_URL = "http://192.168.188.168/solr/mldncore" ;
	public static final String USER_NAME = "mldn" ;	// 用户名
	public static final String PASSWORD = "java" ;	// 密码
	public static final int CONNECTION_TIMEOUT = 6000 ;
	public static final int SOCKET_TIMEOUT = 6000 ;
	public static void main(String[] args) throws Exception {
		// 0、针对于访问路径的认证信息进行配置
		CredentialsProvider provider = new BasicCredentialsProvider() ; // 创建认证对象
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(USER_NAME, PASSWORD) ; // 设置认证的标记
		provider.setCredentials(AuthScope.ANY, credentials); // 在认证提供者对象上设置认证信息
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();// 手工创建HttpClient访问对象
		// 1、SolrJ本身是基于HttpClient的一种访问应用，所以需要依靠HttpSolrClient类来创建一个客户端访问连接
		HttpSolrClient solrClient = new HttpSolrClient.Builder(SOLR_HOST_URL).withConnectionTimeout(CONNECTION_TIMEOUT)
				.withSocketTimeout(SOCKET_TIMEOUT).withHttpClient(httpClient).build();
		// 2、进行Solr查询可以利用SolrQuery来完成
		SolrQuery query = new SolrQuery() ; // 创建数据查询对象
		query.setQuery("*:*") ; // 查询全部内容
		// 3、当执行查询之后一定要有一个数据的返回结果，需要接收返回的数据回应
		QueryResponse response = solrClient.query(query) ; // 发送请求
		// 4、所有的回应的数据信息实际上都在response中包含了，需要通过回应对象获取信息
		SolrDocumentList documents = response.getResults() ;	// 获取所有的Solr返回内容
		// 5、通过SolrDocumentList获取Solr返回结果
		System.out.println("【数据行数】" + documents.getNumFound());
		// 6、获取全部的返回内容 
		for (SolrDocument doc : documents) {
			System.out.println("【返回信息】id = " + doc.get("id") + "、name = " + doc.get("solr_s_name") + "、catalog = "
					+ doc.get("solr_s_catalog"));
		}
		solrClient.close(); 
	}
}
