/**
 * Responsible for handling tag-related operations
 */

jgivenReportApp.factory('tagService', ['dataService', function (dataService) {
  'use strict';

  /**
   * Maps tag IDs to tags and their scenarios
   */
  var tagScenarioMap = getTagScenarioMap(dataService.getTestCases());

  /**
   * Maps tag keys to tag nodes
   */
  var tagNodeMap = {};

  /**
   * Maps tag names to list of tags with the same name
   */
  var tagNameMap = {};

  /**
   * An array of root tags
   */
  var rootTags;

  /**
   * Goes through all scenarios to find all tags.
   * For each tag found, builds up a map of tag keys to tag entries,
   * where a tag entry contains the tag definition and the list of scenarios
   * that are tagged with that tag
   */
  function getTagScenarioMap (scenarios) {
    var tagScenarioMap = {};
    _.forEach(scenarios, function (testCase) {
      _.forEach(testCase.scenarios, function (scenario) {

        scenario.tags = [];

        _.forEach(scenario.tagIds, function (tagId) {

          var tag = addEntry(tagId).tag;
          scenario.tags.push(tag);

          function addEntry (tagId) {
            var tag = getTagByTagId(tagId);
            var tagKey = getTagKey(tag);
            var tagEntry = tagScenarioMap[tagKey];
            if (!tagEntry) {
              tagEntry = {
                tag: tag,
                scenarios: []
              };
              tagScenarioMap[tagKey] = tagEntry;
            }

            if (tagEntry.scenarios.indexOf(scenario) == -1) {
              tagEntry.scenarios.push(scenario);
            }

            _.forEach(tagEntry.tag.tags, function (tagId) {
              addEntry(tagId);
            });

            return tagEntry;
          }

        });
      });
    });
    return tagScenarioMap;
  }

  function getRootTags () {
    if (!rootTags) {
      rootTags = calculateRootTags();
    }
    return rootTags;
  }

  /**
   * Builds up a hierarchy of tag nodes that is shown in the
   * navigation and returns the list of root nodes
   */
  function calculateRootTags () {
    _.forEach(_.values(tagScenarioMap), function (tagEntry) {
      if (tagEntry.tag.hideInNav) return;

      var tagNode = getTagNode(tagEntry);
      var name = getTagName(tagEntry.tag);
      var nameNode = tagNameMap[name];
      if (!nameNode) {
        nameNode = createNameNode(name);
        tagNameMap[name] = nameNode;
      }
      nameNode.addTagNode(tagNode);
    });

    var nameNodesWithMultipleEntries = _.filter(_.values(tagNameMap), function (nameNode) {
      return nameNode.subTags().length > 1;
    });

    _.forEach(nameNodesWithMultipleEntries, function (nameNode) {
      _.forEach(nameNode.subTags(), function (subTag) {
        subTag.nameNode = nameNode;
      });
    });

    var nodesWithoutParents = _.filter(_.values(tagNodeMap), function (tagNode) {
      return undefinedOrEmpty(tagNode.tag().tags) && !tagNode.nameNode;
    });


    return _.sortBy(nameNodesWithMultipleEntries.concat(nodesWithoutParents),
      function (tagNode) {
        return tagNode.nodeName();
      });


    function getTagNode (tagEntry) {
      var tag = tagEntry.tag;
      var key = getTagKey(tag);
      var tagNode = tagNodeMap[key];
      if (!tagNode) {
        tagNode = createTagNode(tagEntry);
        tagNodeMap[key] = tagNode;
        if (tag.tags && tag.tags.length > 0) {
          _.forEach(tag.tags, function (parentTagId) {
            var parentTag = getTagByTagId(parentTagId);
            var parentTagEntry = tagScenarioMap[getTagKey(parentTag)];
            var parentTagNode = getTagNode(parentTagEntry);
            parentTagNode.addTagNode(tagNode);
          });
        }
      }
      return tagNode;
    }

    function createTagNode (tagEntry) {
      var tag = tagEntry.tag;
      var scenarios = tagEntry.scenarios;
      var node = createNode(tagToString(tag));

      node.url = function () {
        return '#tagid/' + window.encodeURIComponent(getTagId(tag)) +
          (tag.value ? '/' + window.encodeURIComponent(tag.value) : '');
      };

      node.scenarios = function () {
        return scenarios;
      };

      node.tag = function () {
        return tag
      };


      return node;
    }

    /**
     * A name node is a pseudo tag node that
     * has as sub nodes all tags with the same name
     */
    function createNameNode (name) {
      var node = createNode(name);

      node.url = function () {
        return '#tag/' + window.encodeURIComponent(name);
      };

      node.scenarios = function () {
        var scenarioMap = {};

        _.forEach(node.subTags(), function (subTag) {
          _.forEach(subTag.scenarios(), function (scenario) {
            scenarioMap[getScenarioId(scenario)] = scenario;
          });
        });

        return _.values(scenarioMap);
      };

      return node;
    }

    function createNode (name) {
      var subTags = [];
      return {

        nodeName: function () {
          return name;
        },

        leafs: function () {
          return _.filter(subTags, function (t) {
            return !t.hasChildren();
          });
        },

        childNodes: function () {
          return _.filter(subTags, function (t) {
            return t.hasChildren();
          });
        },

        hasChildren: function () {
          return subTags.length > 0;
        },

        addTagNode: function (tagNode) {
          subTags.push(tagNode);
        },

        subTags: function () {
          return subTags;
        }
      }
    }
  }

  function getScenariosByTag (tag) {
    return tagScenarioMap[getTagKey(tag)].scenarios;
  }

  function getTagByKey (tagKey) {
    var tagEntry = tagScenarioMap[tagKey];
    return tagEntry && tagEntry.tag;
  }

  function getTagNameNode (name) {
    return tagNameMap[name];
  }

  function getTagByTagId (tagId) {
    var tagInstance = dataService.getTagFile().tags[tagId];
    var tagType = dataService.getTagFile().tagTypeMap[tagInstance.tagType];
    var tag = Object.create(tagType);
    tag.value = tagInstance.value;
    if (tagInstance.description) {
      tag.description = tagInstance.description;
    }
    if (tagInstance.href) {
      tag.href = tagInstance.href;
    }
    return tag;
  }

  return {
    getScenariosByTag: getScenariosByTag,
    getTagByTagId: getTagByTagId,
    getTagByKey: getTagByKey,
    getRootTags: getRootTags,
    getTagNameNode: getTagNameNode
  };
}])
;