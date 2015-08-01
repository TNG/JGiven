describe("TagService", function () {
  beforeEach(module('jgivenReportApp'));

  beforeEach(function () {
    var tagFile = {
      tagTypeMap: {
        'testtag': {
          'type': 'testtag',
          'tags': ['categoryA']
        },
        'categoryA': {
          'type': 'categoryA'
        }
      },
      tags: {
        'testtag-1': {
          tagType: 'testtag',
          value: '1'
        },
        'testtag-2': {
          tagType: 'testtag',
          value: '2'
        },
        'categoryA': {
          tagType: 'categoryA'
        }
      }
    };

    var testCases = [{
      scenarios: [{
        tagIds: ['testtag-1', 'testtag-2']
      }]
    }];

    function createDataServiceMock(tagFile, testCases) {
      return {
        getTagFile: function () {
          return tagFile;
        }
        ,
        getTestCases: function () {
          return testCases;
        }
      }
    }

    module(function ($provide) {
      $provide.value('dataService', createDataServiceMock(tagFile, testCases));
    });


  });

  beforeEach(inject(function (_tagService_) {
    tagService = _tagService_;
  }));

  it("calculates root tags correctly", function () {
    var rootTags = tagService.getRootTags();
    expect(rootTags).toBeDefined();
    expect(rootTags.length).toBe(2);
  });
});