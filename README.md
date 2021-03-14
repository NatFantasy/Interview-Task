## Scenarios
Please check the \MindMap folder for the planning of this task. Here I have used a program called xMind to map out my ideas when approaching the task. I have exported the file as a .pdf so it is more accessible. 

### Setup
1. Java installed
2. Maven installed
3. Download ChromeDriver & put in `C:/chromedriver/` folder (Windows) or `/Users/<Username>/chromedriver/` [Chromedriver Downloads](http://chromedriver.chromium.org/downloads). If not using windows you will need to uncomment the executable path. On Mac you may need to allow Chromedriver in system preferences > Security & Privacy.
4. Download Firefox driver & put in `C:/geckodriver/` folder (Windows) or `/Users/<Username>/geckodriver/` [geckodriver Downloads](https://github.com/mozilla/geckodriver/releases?ref=hackernoon.com). 
5. Download [docker](https://docs.docker.com/get-docker/)

### Run Selenium Grid
Using docker to run Selenium Grid - For my implementation I run selenium hub on local host http://localhost:4444/wd/hub 

To see the grid, go to your browser and go to this url: http://localhost:4444/grid/console

I also link two debug nodes (chrome and firefox) to the hub to see the test running. I have used VNC viewer to debug and view the tests. Download: (https://www.realvnc.com/en/connect/download/viewer/)

```sh
docker run -d -p 4545:4444 --name selenium-hub selenium/hub

docker run -d -P --link  selenium-hub:hub selenium/node-chrome-debug
docker run -d -P --link  selenium-hub:hub selenium/node-firefox-debug
```

To get the port numbers from the running nodes use the below command and use VNC viewer to connect to the instances.

```sh
docker ps -a 
```

### Run
Ensure you are in the correct directory /Interview-Task

```sh
mvn clean test -DsuiteXmlFile=testng/testng.xml
```

### Reporting
After the test has run you can see a test report is published to the /testReports folder.
