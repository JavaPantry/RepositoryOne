RepositoryOne
=============
ExcelTemplateEngine project is a template driven reading excel files engine.

Main purpose for the project is to read Excel files filled by business analysts and convert spreadsheets to java beans which might be saved in database (for example with help of hibernate). Ideally one spreadsheet might provide data for one unit test.

Main class ExcelTemplateHelper aquire template from sheet "SERVICE_SHEET" in constructor and read real data in sheet "Data" with parseData() method

Current version supports multiple tables per workbook.

Check out tagged version 0.1.3.1 - Multiple table support, refactored ClassProperty class to accept json cell descriptor
and run jUnit ExcelTemplateHelperTest (works with ForecastTemplateMultiTab.xls) to get an idea how it works. 
