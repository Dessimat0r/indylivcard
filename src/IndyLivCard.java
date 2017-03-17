import static jodd.jerry.Jerry.jerry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import jodd.jerry.Jerry;
import jodd.jerry.JerryFunction;

public class IndyLivCard {
	public static void main(String[] args) {
		try {
			int page = 0;
			while (true) {
				try (final WebClient webClient = new WebClient()) {
					webClient.getOptions().setJavaScriptEnabled(false);
					URL url = page++ == 0 ?  new URL("http://independent-liverpool.co.uk/our-card/") : new URL("http://independent-liverpool.co.uk/our-card/page/"+(page)+"/");
				    final HtmlPage hpage = webClient.getPage(url);
					final String   text  = hpage.asXml();
					if (text.length() == 0) throw new Exception("No page text recieved");
					Jerry doc = jerry(text);
					boolean finished = doc.$(".no-profiles:first").length() > 0;
					if (finished) {
						System.out.println("Finished.");
						return;
					}
					Jerry previews = doc.$(".thumbnail-content > .preview");
					if (previews.length() == 0) {
						throw new Exception("No previews found on page " + page);
					}
					previews.each(new JerryFunction() {
						@Override
						public Boolean onNode(Jerry $this, int index) {
							System.out.println($this.$(".lead-info,.name").text().replaceAll("[\\s|\\u00A0]+", " ").trim() + ": " + $this.$(".sub-info").text().replaceAll("[\\s|\\u00A0]+", " ").trim());
							return true;
						}
					});
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
