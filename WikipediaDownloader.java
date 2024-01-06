package techcoding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaDownloader implements Runnable{
    private String keyword;

    public WikipediaDownloader(){
    }
    public WikipediaDownloader(String keyword){
        this.keyword = keyword;
    }

    @Override
    public void run() {
        if (this.keyword == null || this.keyword.length() == 0) {
            return;
        }
        //step 1
        this.keyword = this.keyword.trim().replaceAll("[ ]+", "_");

         //step 2
        //https://en.wikipedia.org/wiki/Albert_Einstein
        String wikiUrl = getWikipediaUrlForQuery(this.keyword);
        String response= "";
        String imageUrl = null;
        try {
            //step 3
            String wikipediaResponseHTML = HttpUrlConnectionExample.sendGet(wikiUrl);
          //  System.out.println(wikipediaResponseHTML);
            //step 4
            Document document = Jsoup.parse(wikipediaResponseHTML, "https://en.wikipedia.org");

            Elements childElements = document.body().select(".mw-parser-output > *");

              int state = 0;

            for (Element childElement : childElements) {
                    if (state==0){
                      if (childElement.tagName().equals("table")){
                     state=1;
                  }
                      } else if (state==1) {
                         if(childElement.tagName().equals("p")){
                           state=2;
                           response = childElement.text();
                           break;
                     }
                }
               // System.out.println(childElement.tagName());
            }
            try {
                imageUrl = document.body().select(".infobox img").get(0).attr("src");
            }catch (Exception ex){

            }
        }
         catch (Exception e) {
            e.printStackTrace();
        }
         WikiResult wikiResult = new WikiResult(this.keyword,response,imageUrl);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(wikiResult);
        System.out.println(json);
    }

        private String getWikipediaUrlForQuery (String cleankeyword){
            return "https://en.wikipedia.org/wiki/" + cleankeyword;
        }


    public static void main(String[] args) {
         taskManager taskManager = new taskManager(20);
        WikipediaDownloader wikipediaDownloader =new WikipediaDownloader("Albert Einstein"); ;
        taskManager.waitTillQueueIsFReeAndAddTask(wikipediaDownloader);
    }
}
