package crawling;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;

import object.*;
import org.openqa.selenium.*;
import util.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.chrome.ChromeDriver;


public class NaverCrawling {
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static final String WEB_DRIVER_PATH = "C:\\Users\\KMLEE\\Downloads\\chromedriver.exe";
	public static Map<String, Integer>[] detailMap;
	public static List<Bridge>[] bridgeList;
	public static int cnt = 5;
	public static Set<String> titleSet, fkeySet;
	public static int[] arr = new int[62];
	public static String fkey;
	public static String skey;
	public static List<Feed> listFeed;
	public static final Random random = new Random();
	public static String getUrl(int a, int b){
		if(a==1){
			return "https://search.shopping.naver.com/search/all?frm=NVSHATC&origQuery=%EA%B0%95%EC%95%84%EC%A7%80%EC%82%AC%EB%A3%8C"
					+ "&pagingIndex=" + b
					+ "&pagingSize=40&productSet=total&query=%EA%B0%95%EC%95%84%EC%A7%80%EC%82%AC%EB%A3%8C&sort=rel&timestamp=&viewType=list";

		} else if (a==2) {
			return "https://search.shopping.naver.com/search/all?frm=NVSHATC&origQuery=%EA%B0%95%EC%95%84%EC%A7%80%20%EA%B0%84%EC%8B%9D&pagingIndex=" +
					b +
					"&pagingSize=40&productSet=total&query=%EA%B0%95%EC%95%84%EC%A7%80%20%EA%B0%84%EC%8B%9D&sort=rel&timestamp=&viewType=list";

		}
		return "";


	};

	public static void main(String[] args) throws IOException {
//	System.out.println(NaverCrawling.class.getResource(".").getPath());
		StringBuilder sb = new StringBuilder();
		detailMap = new HashMap[cnt];
		bridgeList = new ArrayList[cnt];
		titleSet = new HashSet<>();
		fkeySet = new HashSet<>();
		listFeed = new ArrayList<>();
		fkey = "";

		//????????????
		////??????, ??????, ?????? , ?????? , ??????, ??????, ???????????? , ???????????????
		////1.??????,??????
		final String EXCELFILE_PATH ="C:/Users/KMLEE/Desktop/daldalhagae/S07P22A302/BE/Crawling/crawling/exceldata";

		String[] feedCol = {"item_sno","name","image","particle_no","grade_no"};
		ExcelFile feedExcel= new ExcelFile("??????&??????",feedCol);
		////2.??????
		String[]  reviewCol= {"user_sno","purchase_no","pet_sno","item_sno","rate","content","image"};
		ExcelFile reviewExcel= new ExcelFile("??????",reviewCol);

		////3.?????? , ??????, ??????, ????????????,????????????
		String[]  effectCol= {"effect_no","name"};
		ExcelFile effectExcel= new ExcelFile("??????",effectCol);
		System.out.println(effectExcel.getWB().hashCode());
		String[]  materialCol= {"material_no","name"};
		ExcelFile materialExcel= new ExcelFile("??????",materialCol);
		String[]  gradeCol= {"grade_no","name"};
		ExcelFile gradeExcel= new ExcelFile("??????",gradeCol);
		String[]  targetCol= {"target_no","name"};
		ExcelFile targetExcel= new ExcelFile("????????????",targetCol);
		String[]  particleCol= {"particle_no","name"};
		ExcelFile particleExcel= new ExcelFile("????????????",particleCol);

		////4.??????????????? (?????? , ??????, ??????, ????????????)
		String[]  effectBridgeCol= {"item_sno","effect_no"};
		ExcelFile effectBridgeExcel= new ExcelFile("??????&??????_??????",effectBridgeCol);
		String[]  materialBridgeCol= {"item_sno","material_no"};
		ExcelFile materialBridgeExcel= new ExcelFile("??????&??????_??????",materialBridgeCol);
		String[]  gradeBridgeCol= {"item_sno","grade_no"};
		ExcelFile gradeBridgeExcel= new ExcelFile("??????_??????",gradeBridgeCol);
		String[]  targetBridgeCol= {"item_sno","target_no"};
		ExcelFile targetBridgeExcel= new ExcelFile("??????_????????????",targetBridgeCol);

		//????????? ?????? ????????????
		FileInputStream fis = new FileInputStream("C:\\Users\\KMLEE\\Desktop\\daldalhagae\\S07P22A302\\BE\\Crawling\\crawling\\exceldata\\pet--739076658.xlsx");
		XSSFWorkbook petExcel = new XSSFWorkbook(fis);
		XSSFSheet petSheet = petExcel.getSheetAt(0);
		XSSFRow petCurrentRow = null;
		XSSFCell petCurrentCell = null;
		int petRows = petSheet.getPhysicalNumberOfRows();
		List<UserPetSno> userPetSnoList= new ArrayList<>();
		for(int idx = 1; idx <petRows; idx++){
			petCurrentRow = petSheet.getRow(idx);
			userPetSnoList.add(new UserPetSno(petCurrentRow.getCell(0).getStringCellValue()//pet
							,petCurrentRow.getCell(1).getStringCellValue()));//user
		}
		for (int i = 0; i < 62; i++) {
			if (i < 10) {
				arr[i] = 48 + i;
			} else if (i >= 10 && i < 36) {
				arr[i] = 55 + i;
			} else {
				arr[i] = 61 + i;
			}
		}

		for (int i = 0; i < cnt; i++) {
			detailMap[i] = new HashMap<>();
			bridgeList[i] = new ArrayList<>();
		}

		try {
			System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		ChromeOptions options = new ChromeOptions();
//		options.addArguments("headless");
//		WebDriver driver = new ChromeDriver(options);
		WebDriver driver = new ChromeDriver();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		for(int x = 1; x<=2;x++) {
			sb.append("-----------------------------------------------");
			sb.append("\n");

			for (int page = 1; page <= 15; page++) { //?????? ????????? ??? ??????
				// ?????? ???????????? html??? ?????? ??? ?????????
				String url=getUrl(x,page);
				driver.get(url);


				for (int i = 0; i <50 ; i++) {
					js.executeScript("window.scrollBy(0," + (300 + i * 20) + ")");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

				// ?????? ????????? ?????????
				List<WebElement> el1 = driver.findElements(By.className("basicList_item__0T9JD"));
				for (int i = 0; i < el1.size(); i++) {
					// ??? ?????? ????????????
					WebElement innerDiv = el1.get(i).findElement(By.className("basicList_inner__xCM3J"));

					// ?????? ????????????
					String title = innerDiv.findElement(By.className("basicList_title__VfX3c")).getText().replaceAll("\\s+\\+\\s", "+").replaceAll(
							"[0-9]+(?i)g\\s*|[0-9]*.[0-9]+(?i)kg|\\[.*\\]|\\([^\\)]*\\)|[0-9]+???|???[0-9]+",
							"").replaceAll("[0-9]+\\+[0-9]*", "").replaceAll("\\s\\+\\s*", "").replaceAll("\\s{2,}", " ").trim();
					if (titleSet.contains(title))
						continue;
					titleSet.add(title);
					if (x == 1) {
						fkey = "f" + randomKey();
					} else
						fkey = "s" + randomKey();
					while (fkeySet.contains(fkey)) {
						if (x == 1) {
							fkey = "f" + randomKey();
						} else
							fkey = "s" + randomKey();
					}
					fkeySet.add(fkey);


					// ????????? ?????? ????????????
					WebElement imageA = innerDiv.findElement(By.className("thumbnail_thumb__Bxb6Z"));
					WebElement img = imageA.findElement(By.tagName("img"));
					String src = img.getAttribute("src");
					String imageName = "";
					String reviewUrl = imageA.getAttribute("href");

					// ?????????????????????
					WebElement detail = innerDiv.findElement(By.className("basicList_detail_box__OoXKt"));
					String detailText = detail.getText();
					Feed feed = new Feed();


					if (!subDetail(detailText,x,feed))//??????
						continue;
					// ?????? ?????? ??????

					feed.setSno(fkey);
					feed.setTitle(title);
					imageName = ImageTransfrom(src);
					feed.setImage(imageName);
					feed.setReviewUrl(reviewUrl);

					sb.append(feed);
					listFeed.add(feed);
				}
			}
		}
		// ??????,?????? ??????
		String feedPath = EXCELFILE_PATH + "/"+ feedExcel.getFileName()+ ".xlsx";
		for(Feed item :listFeed){
			//??????????????? ??????.{"item_sno","name","image","particle_no","grade_no"}
			feedExcel.makeNewRow(item);
		}
		File file = new File(feedPath);
		FileOutputStream fos = null;
		fos = new FileOutputStream(file);
		feedExcel.getWB().write(fos);
		if (fos != null) fos.close();
		// ????????????
		effectExcel.makeNewRow(detailMap[0]);
		String effectPath = EXCELFILE_PATH + "/"+ effectExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		effectExcel.getWB().write(fos);
		if (fos != null) fos.close();

		gradeExcel.makeNewRow(detailMap[1]);//??????
		effectPath = EXCELFILE_PATH + "/"+ gradeExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		gradeExcel.getWB().write(fos);
		if (fos != null) fos.close();

		materialExcel.makeNewRow(detailMap[2]);//??????
		effectPath = EXCELFILE_PATH + "/"+ materialExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		materialExcel.getWB().write(fos);
		if (fos != null) fos.close();

		particleExcel.makeNewRow(detailMap[3]);//particle
		effectPath = EXCELFILE_PATH + "/"+ particleExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		particleExcel.getWB().write(fos);
		if (fos != null) fos.close();

		targetExcel.makeNewRow(detailMap[4]);//target
		effectPath = EXCELFILE_PATH + "/"+ targetExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		targetExcel.getWB().write(fos);
		if (fos != null) fos.close();

		//???????????????
		effectBridgeExcel.makeNewRow(bridgeList[0]);
		effectPath = EXCELFILE_PATH + "/"+ effectBridgeExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		effectBridgeExcel.getWB().write(fos);
		if (fos != null) fos.close();

		gradeBridgeExcel.makeNewRow(bridgeList[1]);
		effectPath = EXCELFILE_PATH + "/"+ gradeBridgeExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		gradeBridgeExcel.getWB().write(fos);
		if (fos != null) fos.close();

		targetBridgeExcel.makeNewRow(bridgeList[4]);
		effectPath = EXCELFILE_PATH + "/"+ targetBridgeExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		targetBridgeExcel.getWB().write(fos);
		if (fos != null) fos.close();

		materialBridgeExcel.makeNewRow(bridgeList[2]);
		effectPath = EXCELFILE_PATH + "/"+ materialBridgeExcel.getFileName()+ ".xlsx";
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		materialBridgeExcel.getWB().write(fos);
		if (fos != null) fos.close();




		// ??????
		for (int i = 0; i < listFeed.size(); i++) {
			Feed rfeed = listFeed.get(i);
			driver.get(rfeed.getReviewUrl());

			String url = driver.getCurrentUrl();
			System.out.println(url);

			//?????? ??????
			effectPath = EXCELFILE_PATH + "/"+ reviewExcel.getFileName()+ ".xlsx";
			file = new File(effectPath);
			fos = new FileOutputStream(file);
			reviewExcel.getWB().write(fos);
			if (fos != null) fos.close();

			// ????????????????????? ??????
			if (url.contains("smartstore.naver.com")) {
				int cnt =0;

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				List<WebElement> cntList = null;
				try {
					cntList = driver.findElements(
							By.cssSelector(".z7cS6-TO7X > ._27jmWaPaKy > ul > li > a"));
				} catch (Exception e) {
					continue;
				}

				for (int j = 0; j < cntList.size(); j++) {
					String kind = cntList.get(j).getText();
					if (kind.contains("??????")) {
						try {
							String cntStr = cntList.get(j).findElement(By.className("_3HJHJjSrNK")).getText().replaceAll(",","");
							cnt = Integer.parseInt(cntStr);
							System.out.println(cnt);
						} catch (Exception e) {
							System.out.println("sdffsd");
						}
					}
				}

				if(cnt==0) {
					continue;
				}
				int pagingCnt = cnt / 20 + ((cnt % 20 == 0)?0 : 1);

				for (int k = 1; k <= pagingCnt; k++) {
					if (k == 50){//?????? ????????? ????????????(1/2)
						break;
					}

					List<WebElement> reviewList = null;
					WebElement next = null;
					try {
						reviewList = driver.findElements(By.className("_2389dRohZq"));
						next = driver.findElement(By.className("_2Ar8-aEUTq"));
					} catch (Exception e) {
						break;
					}

					for (int j = 0; j < reviewList.size(); j++) {
						String score = null;
						String content = null;
						try {
							score = reviewList.get(j).findElement(By.className("_15NU42F3kT")).getText();
							WebElement contentDiv = reviewList.get(j).findElement(By.className("YEtwtZFLDz"));
							content = contentDiv.findElement(By.className("_3QDEeS6NLn")).getText();
						} catch (Exception e) {
							continue;
						}
//						System.out.println("?????????");
//						System.out.println(rfeed.getSno());
//						System.out.println(score);
//						System.out.println(content);
//						System.out.println();
						//?????? ??????.
						//??????????????????

						Review review = new Review();
						int curr = getRandomPetNum();
						review.setPetSno(userPetSnoList.get(curr).getPetSno()); //
						review.setUserSno(userPetSnoList.get(curr).getUserSno()); //
						review.setRate(Integer.parseInt(score));
						review.setContent(content);
						review.setItemSno(rfeed.getSno());
						review.setImage(".");
//						review.setPurchaseNo("");// null
						reviewExcel.makeNewRow(review);

					}

					System.out.println(k);
					System.out.println(pagingCnt);

					if (k == pagingCnt){ //?????? ????????? ?????????
						break;
					}


					try {
						next.click();
						Thread.sleep(500);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}

				}

				// ????????? ????????? ??????
			} else if (url.contains("shopping.naver.com")) {
				int cnt = 0;

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				List<WebElement> cntList = null;
				try {
					cntList = driver.findElements(
							By.cssSelector(".style_detail__nvTm_ > .floatingTab_detail_tab__akl87 > ul > li > a"));
				} catch (Exception e) {
					continue;
				}

				for (int j = 0; j < cntList.size(); j++) {
					String kind = cntList.get(j).findElement(By.tagName("strong")).getText();
					if (kind.equals("???????????????")) {
						try {
							String cntStr = cntList.get(j).findElement(By.tagName("em")).getText().replaceAll(",", "");
							cnt = Integer.parseInt(cntStr);
							System.out.println(cnt);
						} catch (Exception e) {
							System.out.println("sdffsd");
						}
					}
				}

				if(cnt==0) {
					continue;
				}

				int pagingCnt = cnt / 20 + ((cnt % 20 == 0)?0 : 1);

				for (int k = 1; k <= pagingCnt; k++) {
					if (k == 50){//?????? ????????? ????????????(2/2)
						break;
					}

					List<WebElement> reviewList = null;
					try {
						reviewList = driver.findElements(By.cssSelector("#section_review > ul > li"));
					} catch (Exception e) {
						break;
					}
					for (int j = 0; j < reviewList.size(); j++) {


						String score = null;
						String content = null;
						try {
							score = reviewList.get(j).findElement(By.className("reviewItems_average__0kLWX"))
									.getText().replaceAll("??????", "");
							WebElement contentDiv = reviewList.get(j)
									.findElement(By.className("reviewItems_review__DqLYb"));
							content = contentDiv.findElement(By.className("reviewItems_text__XrSSf")).getText();
						} catch (Exception e) {
							continue;
						}
//						System.out.println(rfeed.getSno());
//						System.out.println(score);
//						System.out.println(content);
//						System.out.println();
//						bw.write(String.format("%s|%s|%s",rfeed.getSno(),score,content));
//						bw.write(NEWLINE);

						//?????? ??????.
						//??????????????????
						Review review = new Review();
						int curr = getRandomPetNum();
						review.setPetSno(userPetSnoList.get(curr).getPetSno()); //
						review.setUserSno(userPetSnoList.get(curr).getUserSno()); //
						review.setRate(Integer.parseInt(score));
						review.setContent(content);
						review.setItemSno(rfeed.getSno());
						review.setImage(".");
//						review.setPurchaseNo("");// null

						reviewExcel.makeNewRow(review);


					}

					if (k == pagingCnt){ //?????? ????????? ?????????
						break;
					}


					WebElement aDiv = null;
					try {
						WebElement paginDiv = driver.findElement(By.className("review_section_review__GDdvh"));
						aDiv = paginDiv.findElement(By.cssSelector(".pagination_now__Ey_sR + a"));
					} catch (Exception e) {
						continue;
					}


					try {
						aDiv.click();
						Thread.sleep(500);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}

				}

			}

		}

		sb.append("-------------------\n");
		sb.append(CommonUtil.FUNCTION);
		sb.append("\n");
		sb.append(parse(detailMap[0]));
		sb.append("\n");
		for (Bridge b : bridgeList[0]) {
			sb.append(b);
		}
		sb.append("-------------------\n");

		sb.append(CommonUtil.GRADE);
		sb.append("\n");
		sb.append(parse(detailMap[1]));
		sb.append("\n");
		for (Bridge b : bridgeList[1]) {
			sb.append(b);
		}
		sb.append("-------------------\n");

		sb.append(CommonUtil.MATERIAL);
		sb.append("\n");
		sb.append(parse(detailMap[2]));
		sb.append("\n");
		for (Bridge b : bridgeList[2]) {
			sb.append(b);
		}
		sb.append("-------------------\n");

		sb.append(CommonUtil.PARTICLE);
		sb.append("\n");
		sb.append(parse(detailMap[3]));
		sb.append("\n");
		for (Bridge b : bridgeList[3]) {
			sb.append(b);
		}
		sb.append("-------------------\n");

		sb.append(CommonUtil.TARGET);
		sb.append("\n");
		sb.append(parse(detailMap[4]));
		sb.append("\n");
		for (Bridge b : bridgeList[4]) {
			sb.append(b);
		}
		sb.append("-------------------\n");

		System.out.println(sb);
		//?????? ??????
		file = new File(effectPath);
		fos = new FileOutputStream(file);
		reviewExcel.getWB().write(fos);
		if (fos != null) fos.close();



		// ?????? ??????
		try {
			// ??????????????? null??? ????????????
			if (driver != null) {
				// ???????????? ?????? ??????
				driver.close(); // ???????????? ?????? ??????

				// ???????????? ??????
				driver.quit();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	// ????????? ??????
	public static String ImageTransfrom(String src) {
		// ????????? ?????? ??????
		String folderName = "feedImage";
		String fileName = "";
		BufferedImage saveImage = null;
		File folder = new File(folderName);

		if (!folder.exists()) {
			folder.mkdir();
		}

		// ????????? ???????????? ??????
		try {
			saveImage = ImageIO.read(new URL(src));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		 ?????? ????????? ????????? ????????? ??????
		if (saveImage != null) {
			try {
				int lastIndex = src.lastIndexOf("/");
				int lastIndex2 = src.lastIndexOf("?");
				fileName = src.substring(lastIndex + 1, lastIndex2);
				ImageIO.write(saveImage, "jpg", new File(folderName + "/" + fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (saveImage != null) {
			int lastIndex = src.lastIndexOf("/");
			int lastIndex2 = src.lastIndexOf("?");
			fileName = src.substring(lastIndex + 1, lastIndex2);
		}

		return fileName;
	}

	// ????????? ??????
	public static boolean subDetail(String detailText,int x,Feed feed) {
		String subDetail[] = detailText.split("\\|");
		List<String[]> detailNames = new ArrayList<>();
		boolean gradeCheck = false;

		for (String s1 : subDetail) {
			if (s1 == null || "".equals(s1))
				continue;
			String[] sdd = s1.split(" : ");
			detailNames.add(sdd);

			if (sdd[0].equals(CommonUtil.GRADE)) {
				if (sdd[1].contains("????????????(1??????)") || sdd[1].contains("?????????") || sdd[1].contains("?????????")
						|| sdd[1].contains("???????????????"))
					gradeCheck = true;
			}

		}
		if(x==1 && !gradeCheck) return false;

		for (String[] s : detailNames) {
			setDetail(s,feed);
		}

		return true;
	}

	// ????????? ?????? ?????? ??????
	public static void setDetail(String[] sdd,Feed feed) {

		String[] el;
		switch (sdd[0]) {
		case CommonUtil.FUNCTION:
			el = sdd[1].split(",");

			for (String s : el) {
				if (s.charAt(0) == 32) {
					s = s.substring(1);
				}
				if ("".equals(s)) {
					continue;
				}
				if (!detailMap[0].containsKey(s)) {
					int no = detailMap[0].size() + 1;
					detailMap[0].put(s, no);
				}
				int no = detailMap[0].get(s);
				bridgeList[0].add(new Bridge(fkey, no));

			}

			break;
		case CommonUtil.GRADE:
			el = sdd[1].split(",");

			for (String s : el) {
				if (s.charAt(0) == 32) {
					s = s.substring(1);
				}
				if ("".equals(s)) {
					continue;
				}
				if (!detailMap[1].containsKey(s)) {
					int no = detailMap[1].size() + 1;
					detailMap[1].put(s, no);
				}
				int no = detailMap[1].get(s);
				bridgeList[1].add(new Bridge(fkey, no));
				feed.setGrade(no);
			}

			break;
		case CommonUtil.MATERIAL:
			el = sdd[1].split(",");

			for (String s : el) {
				if (s.charAt(0) == 32) {
					s = s.substring(1);
				}
				if ("".equals(s)) {
					continue;
				}
				if (!detailMap[2].containsKey(s)) {
					int no = detailMap[2].size() + 1;
					detailMap[2].put(s, no);
				}
				int no = detailMap[2].get(s);
				bridgeList[2].add(new Bridge(fkey, no));
			}


			break;
		case CommonUtil.PARTICLE:
			el = sdd[1].split(",");

			for (String s : el) {
				if (s.charAt(0) == 32) {
					s = s.substring(1);
				}
				if ("".equals(s)) {
					continue;
				}
				if (!detailMap[3].containsKey(s)) {
					int no = detailMap[3].size() + 1;
					detailMap[3].put(s, no);
				}
				int no = detailMap[3].get(s);
				bridgeList[3].add(new Bridge(fkey, no));
				feed.setParticleSize(no);
			}

			break;
		case CommonUtil.TARGET:
			el = sdd[1].split(",");

			for (String s : el) {
				if (s.charAt(0) == 32) {
					s = s.substring(1);
				}
				if ("".equals(s)) {
					continue;
				}
				if (!detailMap[4].containsKey(s)) {
					int no = detailMap[4].size() + 1;
					detailMap[4].put(s, no);
				}
				int no = detailMap[4].get(s);
				bridgeList[4].add(new Bridge(fkey, no));
			}

			break;

		default:
			break;
		}

	}

	// map -> String ???????????? ??????
	public static String parse(Map<String, Integer> map) {
		StringBuilder sb = new StringBuilder();

		List<Map.Entry<String, Integer>> entryList = new LinkedList<>(map.entrySet());
		entryList.sort((o1, o2) -> o1.getValue() - o2.getValue());
		for (Map.Entry<String, Integer> entry : entryList) {
			sb.append(entry.getValue());
			sb.append(", ");
			sb.append(entry.getKey());
			sb.append("\n");
		}

		return sb.toString();
	}

	// ?????? ??? ??????
	public static String randomKey() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			int ran = (int) (Math.random() * 62);
			sb.append((char) arr[ran]);
		}

		return sb.toString();
	}
	public static int getRandomPetNum(){
		return random.nextInt(3000); //0~2999
	}


	///////////////////////////img ????????????

//	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
//	public static final String WEB_DRIVER_PATH = "C:\\Users\\KMLEE\\Downloads\\chromedriver.exe";
//	public static void main(String[] args) throws IOException, InterruptedException {
//		//???????????? ????????????
//		FileInputStream fis = new FileInputStream("C:\\Users\\KMLEE\\Desktop\\daldalhagae\\S07P22A302\\BE\\Crawling\\crawling\\exceldata\\??????&??????.xlsx");
//		XSSFWorkbook feedExcel = new XSSFWorkbook(fis);
//		XSSFSheet feedSheet = feedExcel.getSheetAt(0);
//		XSSFRow feedCurrentRow = null;
//		XSSFCell feedCurrentCell = null;
//		int feedRows = feedSheet.getPhysicalNumberOfRows();
//		List<String> imgurlList  = new ArrayList<>();
//		List<String> nameList = new ArrayList<>();
//
//		for(int idx = 1; idx <feedRows; idx++){
//			feedCurrentRow = feedSheet.getRow(idx);
//			String name = feedCurrentRow.getCell(1).getStringCellValue();//??????
//			nameList.add(name);
//		}
//		//selenium??????
//		// ??????, ??????
//		try {
//			System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		WebDriver driver = new ChromeDriver();
//		driver.get("https://search.shopping.naver.com/search/all?query=%EA%B0%95%EC%95%84%EC%A7%80%20%EC%82%AC%EB%A3%8C&cat_id=&frm=NVSHATC");
//		for(String name : nameList){
//			int errcnt =0;
//			while (true) {
//				try {
//				WebElement input = driver.findElement(By.cssSelector("#__next > div > div.header_header__22qGI > div > div.gnb_header_shop__EccVa > div > div.gnbSearch_search_area__CkjUR > form > fieldset > div.gnbSearch_inner__MIZmJ > input"));
//				input.clear();
//				input.sendKeys(name);
//				input.sendKeys(Keys.ENTER);
//				Thread.sleep(1000);
//				WebElement firstDiv = driver.findElement(By.cssSelector("#__next > div > div.style_container__UxP6u > div > div.style_content_wrap__Cdqnl > div.style_content__xWg5l > ul > div > div:nth-child(1)"));
//				String url = firstDiv.findElement(By.tagName("img")).getAttribute("src");
//				imgurlList.add(url);
//				break;
//				} catch (Exception e) {
//					errcnt++;
//					System.out.println("????????????,?????????");;
//					driver.get("https://search.shopping.naver.com/search/all?query=%EA%B0%95%EC%95%84%EC%A7%80%20%EC%82%AC%EB%A3%8C&cat_id=&frm=NVSHATC");
//					Thread.sleep(5000);
//					if(errcnt==2){ //2????????????
//						imgurlList.add("fail");
//						System.out.println("fail,pass");
//						break;
//					}
//				}
//			}
//
//		}
//		int idx = 1;
//		for(String url : imgurlList) {
//			feedSheet.getRow(idx++).getCell(2).setCellValue(url);
//		}
//		//???????????? ????????????
//		String Path = "C:\\Users\\KMLEE\\Desktop\\data\\feed_fixed.xlsx";
//		File file = new File(Path);
//		FileOutputStream fos = new FileOutputStream(file);
//		feedExcel.write(fos);
//		if (fos != null) fos.close();
//	}
}
