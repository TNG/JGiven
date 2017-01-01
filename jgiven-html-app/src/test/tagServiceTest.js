import _ from 'lodash/core'
import { getTagKey } from '../js/util.js'
import TagService from '../js/service/tagService.js'

describe("TagService", function () {
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

  var tagFile = {
    tagTypeMap: {
      'issue': {
        'type': 'issue',
        'description': 'issue description'
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

  var tagService;

  beforeEach(function() {
    tagService = TagService(createDataServiceMock(tagFile, testCases));
  });

  it("calculates root tags correctly", function () {

    /** Expected Tag Tree:
     + categoryA
     --+ categoryA1
     --+ featureA
     + categoryB
     --+ featureB
     + feature
     --+ featureA
     --+ featureB
     + issue
     --+ issue-1
     --+ issue-2
     */

    var rootTags = tagService.getRootTags();
    expect(rootTags).toBeDefined();
    var rootTagNames = _.map(rootTags, function (rootTag) {
      return rootTag.nodeName();
    });
    expect(rootTagNames).toEqual(['categoryA', 'categoryB', 'feature', 'issue', 'someA']);
  });

  it("return tags with descriptions", function () {
    var issueTag = tagService.getTagByKey('issue-1');
    expect(issueTag).toBeDefined();
    expect(issueTag.type).toBe('issue');
    expect(issueTag.description).toEqual('issue description');
  });

  it("returns scenarios with correct tags", function () {
    var scenarios = tagService.getScenariosByTag(tagService.getTagByKey('issue-1'));
    expect(scenarios.length).toEqual(1);
    expect(scenarios[0].tags.length).toEqual(testCases[0].scenarios[0].tagIds.length);
    expect(_.map(scenarios[0].tags, getTagKey)).toEqual(['issue-1', 'issue-2', 'categoryB', 'featureA-A', 'featureB-B', 'somethingA-someA']);
  });

});