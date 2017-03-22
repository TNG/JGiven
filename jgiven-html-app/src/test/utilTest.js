import { nanosToReadableUnit, splitClassName, parseNextInt, replaceArguments, getArgumentInfos, getThumbnailPath, isImageType } from '../js/util.js'

describe("Util", function () {

  describe("nanosToReadableUnit", function () {

    var ONE_MS_IN_NS = 1000000;
    var ONE_SECOND_IN_NS = ONE_MS_IN_NS * 1000;
    var ONE_MINUTE_IN_NS = ONE_SECOND_IN_NS * 60;
    var ONE_HOUR_IN_NS = ONE_MINUTE_IN_NS * 60;

    it("works for ms", function () {
      expect(nanosToReadableUnit(ONE_MS_IN_NS)).toEqual("1ms");
      expect(nanosToReadableUnit(ONE_MS_IN_NS * 251)).toEqual("251ms");
    });

    it("works for seconds", function () {
      expect(nanosToReadableUnit(ONE_SECOND_IN_NS)).toEqual("1.000s");
      expect(nanosToReadableUnit(ONE_SECOND_IN_NS * 2 + 50 * ONE_MS_IN_NS)).toEqual("2.050s");
    });

    it("works for minutes", function () {
      expect(nanosToReadableUnit(ONE_MINUTE_IN_NS)).toEqual("1:00min");
      expect(nanosToReadableUnit(ONE_MINUTE_IN_NS * 2 + 2 * ONE_SECOND_IN_NS)).toEqual("2:02min");
      expect(nanosToReadableUnit(ONE_MINUTE_IN_NS * 2 + 10 * ONE_SECOND_IN_NS)).toEqual("2:10min");
    });

    it("works for hours", function () {
      expect(nanosToReadableUnit(ONE_HOUR_IN_NS)).toEqual("1:00h");
      expect(nanosToReadableUnit(ONE_HOUR_IN_NS * 5 + 3 * ONE_MINUTE_IN_NS)).toEqual("5:03h");
      expect(nanosToReadableUnit(ONE_HOUR_IN_NS * 5 + 20 * ONE_MINUTE_IN_NS)).toEqual("5:20h");
    });

  });

  describe("splitClassName", function() {
      it("works for classes without packages", function() {
          var t = splitClassName("test");
          expect(t.className).toEqual("test");
          expect(t.packageName).toEqual("");
      });
  });

  describe("getArgumentInfos", function() {
      it("works for words which are arguments", function() {
          function argumentName(str) { return { "argumentName" : str }; }

          var words1 = [ { "value" : 1, "argumentInfo": argumentName("i") } ];
          var words2 = [ { "value" : 2, "argumentInfo": argumentName("i") }
                       , { "value" : "str", "argumentInfo" : argumentName("string") }
                       , { "value" : -10, "argumentInfo" : argumentName("neg_integer") }
                       , { "value" : "", "argumentInfo" : argumentName("empty") }
                       ];
          var enumRes1 = [], nameRes1 = [], enumRes2 = [], nameRes2 = [];

          var [enumRes1, nameRes1] = getArgumentInfos(words1);
          expect(enumRes1.length).toEqual(1);
          expect(nameRes1["i"]).toEqual(1);


          var [enumRes2, nameRes2] = getArgumentInfos(words2);
          expect(enumRes2.length).toEqual(4);
          expect(enumRes2[0]).toEqual(2);
          expect(enumRes2[1]).toEqual("str");
          expect(enumRes2[2]).toEqual(-10);
          expect(enumRes2[3]).toEqual("");

          expect(nameRes2["i"]).toEqual(2);
          expect(nameRes2["string"]).toEqual("str");
          expect(nameRes2["neg_integer"]).toEqual(-10);
          expect(nameRes2["empty"]).toEqual("");
      });

      it("works for words which are not arguments", function() {
          var words1 = [ { "value" : 1 } ];
          var words2 = [ { "value" : 1 }
                       , { "value" : "str" }
                       , { "value" : -10 }
                       , { "value" : "" }
                       ];
          var enumRes1 = [], nameRes1 = [], enumRes2 = [], nameRes2 = [];
          var [enumRes1, nameRes1] = getArgumentInfos(words1);
          expect(enumRes1.length).toEqual(0);

          var [enumRes2, nameRes2] = getArgumentInfos(words2);
          expect(enumRes1.length).toEqual(0);
      });

      it("works for mixed words and arguments", function() {
          function argumentName(str) { return { "argumentName" : str }; }

          var words1 = [ { "value" : 1, "argumentInfo": argumentName("i") }
                       , { "value" : " " }
                       ];
          var words2 = [ { "value" : "Given"}
                       , { "value" : 2, "argumentInfo": argumentName("i") }
                       , { "value" : "then do something..." }
                       , { "value" : -10, "argumentInfo" : argumentName("neg_integer") }
                       , { "value" : "and after that it returns"}
                       , { "value" : "nothing", "argumentInfo" : argumentName("empty") }
                       ];
          var enumRes1 = [], nameRes1 = [], enumRes2 = [], nameRes2 = [];

          [enumRes1, nameRes1] = getArgumentInfos(words1);
          expect(enumRes1[0]).toEqual(1);
          expect(nameRes1["i"]).toEqual(1);

          [enumRes2, nameRes2] = getArgumentInfos(words2);
          expect(enumRes2.length).toEqual(3);
          expect(enumRes2[0]).toEqual(2);
          expect(enumRes2[1]).toEqual(-10);
          expect(enumRes2[2]).toEqual("nothing");

          expect(nameRes2["i"]).toEqual(2);
          expect(nameRes2["neg_integer"]).toEqual(-10);
          expect(nameRes2["empty"]).toEqual("nothing");
      });
  });

  describe("parseNextInt", function() {
       it("works for digits", function() {
            var res1 = parseNextInt("0");
            expect(res1.integer).toEqual(0);
            expect(res1.length).toEqual(1);

            var res2 = parseNextInt("1");
            expect(res2.integer).toEqual(1);
            expect(res2.length).toEqual(1);

            var res3 = parseNextInt("3ab c");
            expect(res3.integer).toEqual(3);
            expect(res3.length).toEqual(1);
       });

       it("works for different strings", function() {
            var res1 = parseNextInt("");
            expect(res1.integer).toEqual(undefined);
            expect(res1.length).toEqual(0);

            var res2 = parseNextInt("abc");
            expect(res2.integer).toEqual(undefined);
            expect(res2.length).toEqual(0);

            var res3 = parseNextInt("a 2 c 3 d 4");
            expect(res3.integer).toEqual(undefined);
            expect(res3.length).toEqual(0);

            var res4 = parseNextInt("a 234");
            expect(res4.integer).toEqual(undefined);
            expect(res4.length).toEqual(0);
       });

       it("works for different integer edge cases", function() {
            var res1 = parseNextInt("10");
            expect(res1.integer).toEqual(10);
            expect(res1.length).toEqual(2);

            var res2 = parseNextInt("01");
            expect(res2.integer).toEqual(1);
            expect(res2.length).toEqual(2);

            var res3 = parseNextInt("1001");
            expect(res3.integer).toEqual(1001);
            expect(res3.length).toEqual(4);

            var res4 = parseNextInt("01abc");
            expect(res4.integer).toEqual(1);
            expect(res4.length).toEqual(2);

            var res3 = parseNextInt("9876543210");
            expect(res3.integer).toEqual(9876543210);
            expect(res3.length).toEqual(10);

            var res5 = parseNextInt("05 32abc");
            expect(res5.integer).toEqual(5);
            expect(res5.length).toEqual(2);

            var res6 = parseNextInt("05-32abc");
            expect(res6.integer).toEqual(5);
            expect(res6.length).toEqual(2);
       });
  });

  describe("replaceArguments works for the same tests as the AS description", function(){
      // the only difference is that the unused arguments are not appended

      it("Placeholder with index", function() {
          var value = "$1";
          var nameArray = [];
          var enumArray = [1, 2];
          nameArray["a"] = enumArray[0];
          nameArray["b"] = enumArray[1];
          var expectedValue = "1";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });


      it("Placeholder without index", function() {
          var value = "$"
          var nameArray = [];
          var enumArray = [1, 2];
          nameArray["a"] = enumArray[0];
          nameArray["b"] = enumArray[1];
          var expectedValue = "1";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Escaped placeholder", function() {
          var value = "$$"
          var nameArray = [];
          var enumArray = [1, 2];
          nameArray["a"] = enumArray[0];
          nameArray["b"] = enumArray[1];
          var expectedValue = "$";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Multiple placeholders with switch order", function() {
          var value = "$2 + $1"
          var nameArray = [];
          var enumArray = [1, 2];
          nameArray["a"] = enumArray[0];
          nameArray["b"] = enumArray[1];
          var expectedValue = "2 + 1";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Placeholders with additional text", function() {
          var value = "a = $1 and b = $2"
          var nameArray = [];
          var enumArray = [1, 2];
          nameArray["a"] = enumArray[0];
          nameArray["b"] = enumArray[1];
          var expectedValue = "a = 1 and b = 2";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Placeholders references by argument names in order", function() {
          var value = "int = $int and str = $str and bool = $bool"
          var nameArray = [];
          var enumArray = [1, "some string", true];
          nameArray["int"] = enumArray[0];
          nameArray["str"] = enumArray[1];
          nameArray["bool"] = enumArray[2];
          var expectedValue = "int = 1 and str = some string and bool = true";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Placeholders references by argument names in mixed order", function() {
          var value = "str = $str and int = $int and bool = $bool"
          var nameArray = [];
          var enumArray = [1, "some string", true];
          nameArray["int"] = enumArray[0];
          nameArray["str"] = enumArray[1];
          nameArray["bool"] = enumArray[2];
          var expectedValue = "str = some string and int = 1 and bool = true";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Placeholders references by argument names and enumeration", function() {
          var value = "str = $str and int = $1 and bool = $bool"
          var nameArray = [];
          var enumArray = [1, "some string", true];
          nameArray["int"] = enumArray[0];
          nameArray["str"] = enumArray[1];
          nameArray["bool"] = enumArray[2];
          var expectedValue = "str = some string and int = 1 and bool = true";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Placeholders references by argument names and enumerations", function() {
          var value = "bool = $3 and str = $2 and int = $int"
          var nameArray = [];
          var enumArray = [1, "some string", true];
          nameArray["int"] = enumArray[0];
          nameArray["str"] = enumArray[1];
          nameArray["bool"] = enumArray[2];
          var expectedValue = "bool = true and str = some string and int = 1";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Placeholder without index mixed with names", function() {
          var value = "bool = $bool and int = $ and str = $"
          var nameArray = [];
          var enumArray = [1, "some string", true];
          nameArray["int"] = enumArray[0];
          nameArray["str"] = enumArray[1];
          nameArray["bool"] = enumArray[2];
          var expectedValue = "bool = true and int = 1 and str = some string";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Placeholder without index mixed with names and index", function() {
          var value = "bool = $bool and str = $2 and int = $ and str = $ and bool = $3"
          var nameArray = [];
          var enumArray = [1, "some string", true];
          nameArray["int"] = enumArray[0];
          nameArray["str"] = enumArray[1];
          nameArray["bool"] = enumArray[2];
          var expectedValue = "bool = true and str = some string and int = 1 and str = some string and bool = true";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Placeholder with unknown argument names get erased", function() {
          var value = "bool = $bool and not known = $unknown and unknown = $10"
          var nameArray = [];
          var enumArray = [1, "some string", true];
          nameArray["int"] = enumArray[0];
          nameArray["str"] = enumArray[1];
          nameArray["bool"] = enumArray[2];
          var expectedValue = "bool = true and not known = 1 and unknown = some string";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("Non-Java-Identifier char does trigger a space after a placeholder", function() {
          var value = "$]"
          var nameArray = [];
          var enumArray = [1];
          nameArray["int"] = enumArray[0];
          var expectedValue = "1 ]";
          expect(replaceArguments(value, enumArray, nameArray)).toEqual(expectedValue);
      });

      it("works for empty argument arrays", function() {
          var enumArray = [];
          var nameArray = [];
          expect(replaceArguments("", enumArray, nameArray)).toEqual("");
          expect(replaceArguments("abc", enumArray, nameArray)).toEqual("abc");
          expect(replaceArguments("a b c", enumArray, nameArray)).toEqual("a b c");
          expect(replaceArguments("$", enumArray, nameArray)).toEqual("$");
          expect(replaceArguments("$$", enumArray, nameArray)).toEqual("$");
          expect(replaceArguments("$1", enumArray, nameArray)).toEqual("$1");
          expect(replaceArguments("$1 $2", enumArray, nameArray)).toEqual("$1 $2");
      });

  });

  describe("substituteThumbnailPath", function() {
      it("works for paths with no unusual dots", function() {
           var path0 = "/simple/data/path.png";
           expect(getThumbnailPath(path0)).toEqual("/simple/data/path-thumb.png")

           var path1 = "/simple/data/path0.png";
           expect(getThumbnailPath(path1)).toEqual("/simple/data/path0-thumb.png");

           var path2 = "/simple/data/path1.png";
           expect(getThumbnailPath(path2)).toEqual("/simple/data/path1-thumb.png")

           var path3 = "/simple/data/path01.png";
           expect(getThumbnailPath(path3)).toEqual("/simple/data/path01-thumb.png");

           var path4 = "/simple/data/path1091.png";
           expect(getThumbnailPath(path4)).toEqual("/simple/data/path1091-thumb.png");
      });

      it("works for paths with multiple dots", function() {
           var path0 = "/simple/data.custom/current.something/path1.png";
           expect(getThumbnailPath(path0)).toEqual("/simple/data.custom/current.something/path1-thumb.png");

           var path1 = "/simple/data.custom/current.something/path10192.png";
           expect(getThumbnailPath(path1)).toEqual("/simple/data.custom/current.something/path10192-thumb.png");
      });
  });

  describe("isImageType", function() {
      it("works for images mimetypes", function(){
           var mime0 = "image/jpeg";
           var mime1 = "image/png";
           var mime2 = "image/gif";

           expect(isImageType(mime0)).toEqual(true);
           expect(isImageType(mime1)).toEqual(true);
           expect(isImageType(mime2)).toEqual(true);

      });

      it("works for non-image mimetypes", function(){
           var mime0 = "text/html";
           var mime1 = "magnus-internal/imagemap";
           var mime2 = "audio/basic";

           expect(isImageType(mime0)).toEqual(false);
           expect(isImageType(mime1)).toEqual(false);
           expect(isImageType(mime2)).toEqual(false);

      });

  });
});
