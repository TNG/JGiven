import { nanosToReadableUnit, splitClassName, parseNextInt, replaceArguments, getArgumentInfos } from '../js/util.js'

let _ = require('lodash');

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
  /*
  describe("getArgumentInfo", function() {
       it("can parse a simple wordarray", function() {
          var words = [{
                         "value": "Given",
                         "isIntroWord": true
                       },
                       {
                         "value": "argument names can be referenced"
                       },
                       {
                         "value": "false",
                         "argumentInfo": {
                           "argumentName": "bool",
                           "formattedValue": "false"
                         }
                       },
                       {
                         "value": "0",
                         "argumentInfo": {
                           "argumentName": "i",
                           "formattedValue": "0"
                         }
                       }];
          [nameArray, enumArray] = getArgumentInfos(words);
          expect(nameArray["bool"]).toEqual("false");
          expect(nameArray["i"]).toEqual("0");
          expect(nameArray["foo"]).toEqual(undefined);

          expect(enumArray[0]).toEqual("false");
          expect(enumArray[1]).toEqual("1");
          expect(enumArray.length).toEqual(2);
       });
  }); */
 
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

  describe("replaceArguments", function(){
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

      it("works for an enum array with length 1", function() {
          var enumArray = [1];
          var nameArray = [];
          expect(replaceArguments("", enumArray, nameArray)).toEqual("");
          expect(replaceArguments("abc", enumArray, nameArray)).toEqual("abc");
          expect(replaceArguments("a b c", enumArray, nameArray)).toEqual("a b c");
          expect(replaceArguments("$", enumArray, nameArray)).toEqual("1");
          expect(replaceArguments("$$", enumArray, nameArray)).toEqual("$");
          expect(replaceArguments("$1", enumArray, nameArray)).toEqual("1");
          expect(replaceArguments("$1 $2", enumArray, nameArray)).toEqual("1 $2");
          expect(replaceArguments("$2 $1", enumArray, nameArray)).toEqual("$2 1");
          expect(replaceArguments("$2 $", enumArray, nameArray)).toEqual("$2 1");

      });

      it("works for an enum array with length 6", function() {
          var enumArray = [1,2,3,4,5,6];
          var nameArray = [];
          expect(replaceArguments("$ $ $ $ $ $", enumArray, nameArray)).toEqual("1 2 3 4 5 6");
          expect(replaceArguments("$ $ $ $", enumArray, nameArray)).toEqual("1 2 3 4");
          expect(replaceArguments("$ $1 $ $3", enumArray, nameArray)).toEqual("1 1 2 3");
          expect(replaceArguments("$3 $4 $1 $2", enumArray, nameArray)).toEqual("3 4 1 2");
      });

      it("works for arrays with different types", function() {
          var enumArray = ["string", true, 2];
          var nameArray = [];
          expect(replaceArguments("$ $ $", enumArray, nameArray)).toEqual("string true 2");
          expect(replaceArguments("$3 $1 $2", enumArray, nameArray)).toEqual("2 string true");
          expect(replaceArguments("$1 sample $ text $", enumArray, nameArray)).toEqual("string sample string text true");
          expect(replaceArguments("$2 sample $$", enumArray, nameArray)).toEqual("true sample $");
      });

      it("works for more than 9 arguments", function() {
          var enumArray = [1,2,3,4,5,6,7,8,9,10
              ,11,12,13,14,15,16,17,18,19,20
              ,21,22,23,24,25,26,27,28,29,30
              ,31,32,33,34,35,36,37,38,39,40
              ,41,42,43,44,45,46,47,48,49,50
              ,51,52,53,54,55,56,57,58,59,60
              ,61,62,63,64,65,66,67,68,69,70
              ,71,72,73,74,75,76,77,78,79,80
              ,81,82,83,84,85,86,87,88,89,90
              ,91,92,93,94,95,96,97,98,99,100];
          var nameArray = [];
          expect(replaceArguments("#99 $99", enumArray, nameArray)).toEqual("#99 99");
          expect(replaceArguments("$15 $10", enumArray, nameArray)).toEqual("15 10");
          expect(replaceArguments("$98, $1, $2, $54" , enumArray, nameArray)).toEqual("98, 1, 2, 54");
          expect(replaceArguments("$100 minus $43 equals $57", enumArray, nameArray)).toEqual("100 minus 43 equals 57");
      });

      it("works for named placeholder", function() {
          var enumArray = [false, 10, "string"];
          var nameArray = [];
          nameArray["bool"] = false;
          nameArray["int"]  = 10;
          nameArray["str"]  = "string";
          expect(replaceArguments("$bool, $str, $int", enumArray, nameArray)).toEqual("false, string, 10");
          expect(replaceArguments("$bool, $, $int", enumArray, nameArray)).toEqual("false, false, 10");
          expect(replaceArguments("$2, $, $int", enumArray, nameArray)).toEqual("10, false, 10");
          expect(replaceArguments("$2, $, $4", enumArray, nameArray)).toEqual("10, false, $4");
          expect(replaceArguments("$2, $unknown, $", enumArray, nameArray)).toEqual("10, falseunknown, 10");
      })
  });
});
