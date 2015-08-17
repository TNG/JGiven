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

});
