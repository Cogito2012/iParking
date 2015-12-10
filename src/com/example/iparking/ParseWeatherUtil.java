package com.example.iparking;

public class ParseWeatherUtil {

	//省份城市
	private String province;
	private String city;
	private String citycode;
	private String cityimage;
	//今日天气
	private String time;
	private String Temperature1;// 温度
	private String Weather1;// 当天天气
	private String Wind_Detail1;// 当天风向描述;
	private String WeatherGif1_0;  
	private String WeatherGif1_1;
	private String Weather_Detail;
	private String Tip_Detail;
	//明日天气
	private String Temperature2;
	private String Weather2;
	private String Wind_Detail2;
	private String WeatherGif2_0;  
	private String WeatherGif2_1;
	//后天天气
	private String Temperature3;
	private String Weather3;
	private String Wind_Detail3;
	private String WeatherGif3_0;  
	private String WeatherGif3_1;
	//城市文化
	private String cityculture;

	public ParseWeatherUtil(String result) {
		super();
		String results[] = result.replace("string=", "").split(";");
		this.province = results[0];
		this.city = results[1];
		this.citycode = results[2];
		this.cityimage = results[3];
		this.time = results[4];
		this.Temperature1 = results[5];
		this.Weather1 = results[6];
		this.Wind_Detail1 = results[7];
		this.WeatherGif1_0 = results[8];
		this.WeatherGif1_1 = results[9];
		this.Weather_Detail = results[10];
		this.Tip_Detail = results[11];
		this.Temperature2 = results[12];
		this.Weather2 = results[13];
		this.Wind_Detail2 =results[14];
		this.WeatherGif2_0 = results[15];
		this.WeatherGif2_1 = results[16];
		this.Temperature3 = results[17];
		this.Weather3 = results[18];
		this.Wind_Detail3 = results[19];
		this.WeatherGif3_0 = results[20];
		this.WeatherGif3_1 = results[21];
		this.cityculture = results[22];

	}

	///////////////////////////////////////////
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	/////////////////////////////////////////////
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public String getTemperature1() {
		return Temperature1;
	}
	public void setTemperature1(String Temperature1) {
		this.Temperature1 = Temperature1;
	}

	public String getWeather1() {
		return Weather1;
	}
	public void setWeather1(String Weather1) {
		this.Weather1 = Weather1;
	}

	public String getWind_Detail1() {
		return Wind_Detail1;
	}
	public void setWind_Detail1(String Wind_Detail1) {
		this.Wind_Detail1 = Wind_Detail1;
	}

	public String getWeather_Detail(){
		return Weather_Detail;
	}
	public void setWeather_Detail(String Weather_Detail){
		this.Weather_Detail = Weather_Detail;
	}
	
	public String getTip_Detail(){
		return Tip_Detail;
	}
	public void setTip_Detail(String Tip_Detail){
		this.Tip_Detail = Tip_Detail;
	}
	
    /////////////////////////////////////////////
	public String getTemperature2(){
		return Temperature2;
	}
	public void setTemperature2(String Temperature2) {
		this.Temperature2 = Temperature2;
	}

	public String getWeather2() {
		return Weather2;
	}
	public void setWeather2(String Weather2) {
		this.Weather2 = Weather2;
	}

	public String getWind_Detail2() {
		return Wind_Detail2;
	}
	public void setWind_Detail2(String Wind_Detail2) {
		this.Wind_Detail2 = Wind_Detail2;
	}

	/////////////////////////////////////////////
	public String getTemperature3(){
		return Temperature3;
	}
	public void setTemperature3(String Temperature3) {
		this.Temperature3 = Temperature3;
	}
	
	public String getWeather3() {
		return Weather3;
	}
	public void setWeather3(String Weather3) {
		this.Weather3 = Weather3;
	}
	
	public String getWind_Detail3() {
		return Wind_Detail3;
	}
	public void setWind_Detail3(String Wind_Detail3) {
		this.Wind_Detail3 = Wind_Detail3;
	}
	
	//////////////////////////////////////////////////
	public String getcityculture(){
		return cityculture;
	}
	public void setcityculture(String cityculture){
		this.cityculture = cityculture;
	}
}
