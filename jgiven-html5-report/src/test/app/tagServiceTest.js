describe("TagService", function () {
  beforeEach(module('jgivenReportApp'));

  beforeEach(function () {
    var tagFile = {
      tagTypeMap: {
        'issue': {
          'type': 'issue',
          'tags': ['categoryA1']
        },
        'categoryA': {
          'type': 'categoryA'
        },
        'categoryB': {
          'type': 'categoryB'
        },
        'categoryA1': {
          'type': 'categoryA1',
          'tags': ['categoryA']
        },
        'featureA': {
          'type': 'featureA',
          'name': 'feature',
          'tags': ['categoryA']
        },
        'featureB': {
          'type': 'featureB',
          'name': 'feature',
          'tags': ['categoryB']
        },
        'somethingA': {
          'type': 'somethingA',
          'name': 'something'
        }
      },
      tags: {
        'issue-1': {
          'tagType': 'issue',
          'value': '1'
        },
        'issue-2': {
          'tagType': 'issue',
          'value': '2'
        },
        'categoryA': {
          'tagType': 'categoryA'
        },
        'categoryA1': {
          'tagType': 'categoryA1'
        },
        'categoryB': {
          'tagType': 'categoryB'
        },
        'featureA': {
          'tagType': 'featureA',
          'value': 'A'
        },
        'featureB': {
          'tagType': 'featureB',
          'value': 'B'
        },
        'somethingA': {
          'tagType': 'somethingA',
          'value': 'someA'
        }

      }
    };

    var testCases = [{
      scenarios: [{
        tagIds: ['issue-1', 'issue-2', 'categoryB', 'featureA', 'featureB', 'somethingA']
      }]
    }];

    /** Expected Tag Tree:
     + categoryA
     --+ categoryA1
     ----+ issue
     ------+ issue-1
     ------+ issue-2
     --+ featureA
     + categoryB
     --+ featureB
     + feature
     --+ featureA
     --+ featureB
     */


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
    var rootTagNames = _.map(rootTags, function (rootTag) {
      return rootTag.nodeName();
    });
    expect(rootTagNames).toEqual(['categoryA', 'categoryB', 'feature', 'someA']);
  });
});