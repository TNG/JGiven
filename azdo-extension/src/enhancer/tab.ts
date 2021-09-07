import Controls = require("VSS/Controls");
import TFS_Build_Contracts = require("TFS/Build/Contracts");
import TFS_Build_Extension_Contracts = require("TFS/Build/ExtensionContracts");
import DT_Client = require("TFS/DistributedTask/TaskRestClient");
import { TaskAttachment } from "TFS/DistributedTask/Contracts";

let _currentInfoTab: InfoTab;

export class InfoTab extends Controls.BaseControl {
	private locationTranslation: Map<string, string> = new Map()
	private jgivenFileToLocalPath: Map<String, string>
	private toBeIncludedInHTML: Array<any> = []
	private vsoContext: WebContext
	private build: TFS_Build_Contracts.Build
	private taskClient: DT_Client.TaskHttpClient4_1

	constructor() {
		super();
	}

	public initialize(): void {
		super.initialize();
		let sharedConfig: TFS_Build_Extension_Contracts.IBuildResultsViewExtensionConfig = VSS.getConfiguration();
		this.vsoContext = VSS.getWebContext();

		if (sharedConfig) {
			sharedConfig.onBuildChanged((_build: TFS_Build_Contracts.Build) => {
				_currentInfoTab = this
				this.build = _build
				this._initBuildInfo(_build);
				this.taskClient = DT_Client.getClient();
				this.getJGivenFileToLocalPathTranslations()
					.then(this.getAllUserAttachmentLocations)
					.then(this.getScriptDefinitions)
					.then(this.updateHtml)
			});
		}
	}

	private async updateHtml() {
		let allHtml = await _currentInfoTab.getAttachmentContents("html");
		let jgivenHtml: string = allHtml[0];
		jgivenHtml = _currentInfoTab.getNewLocations(jgivenHtml);
		let jgivenHtmlElement = new DOMParser().parseFromString(jgivenHtml, "text/html");
		for (let script of _currentInfoTab.toBeIncludedInHTML) {
			jgivenHtmlElement.body.append(script);
		}
		document.documentElement.remove()
		let iframe = document.createElement("iframe")
		iframe.srcdoc = jgivenHtmlElement.documentElement.outerHTML
		iframe.style.height = "100%"
		iframe.style.width = "100%"
		iframe.style.border = "0"
		document.write(iframe.outerHTML)
	}

	private getNewLocations(content: string): string {
		let index: number = content.indexOf('data/');
		while (index != -1) {
			content = content.slice(0, index) + content.slice(index + 5);
			index = content.indexOf('data/');
		}

		return content
	}

	private async getJGivenFileToLocalPathTranslations() {
		let attachmentContents = await _currentInfoTab.getAttachmentContents("locationMappings")
		let jsonString: string = attachmentContents[0]
		_currentInfoTab.jgivenFileToLocalPath = new Map(JSON.parse(jsonString));
	}

	private async getAllUserAttachmentLocations() {
		let attachmentLinks = await _currentInfoTab.getAttachmentLinks('attachmentFile')
		$.each(attachmentLinks, (index, attachmentLink) => {
			let jgivenFileName: string = attachmentLink.slice(attachmentLink.indexOf("jgivenFile_"));
			let localPath: string = _currentInfoTab.jgivenFileToLocalPath.get(jgivenFileName)
			_currentInfoTab.locationTranslation.set(localPath, attachmentLink)
		})
	}

	private async getAttachmentsWithType(type: string) {
		let taskAttachments: TaskAttachment[] = []
		await _currentInfoTab.taskClient.getPlanAttachments(_currentInfoTab.vsoContext.project.id, "build", _currentInfoTab.build.orchestrationPlan.planId, type).then((attachments) => {
			taskAttachments = attachments;
		});

		return taskAttachments;
	}

	private async getAttachmentContents(type: string) {
		let attachmentContents: Array<string> = []
		let allAttachments: TaskAttachment[] = await _currentInfoTab.getAttachmentsWithType(type)
		for (let attachment of allAttachments) {
			if (attachment._links && attachment._links.self && attachment._links.self.href) {
				let recordId = attachment.recordId;
				let timelineId = attachment.timelineId;
				let attachmentName = attachment.name;

				await _currentInfoTab.taskClient.getAttachmentContent(_currentInfoTab.vsoContext.project.id, "build", _currentInfoTab.build.orchestrationPlan.planId,
					timelineId, recordId, type, attachmentName).then(attachmentContent => {
						attachmentContents.push(_currentInfoTab.getPlainTextFromArrayBuffer(attachmentContent))
					});
			}
		}

		return attachmentContents
	}

	private async getAttachmentLinks(type: string) {
		let attachmentLinks: Array<string> = []
		let attachmentWithTypes = await _currentInfoTab.getAttachmentsWithType(type)
		$.each(attachmentWithTypes, (index, attachment) => {
			if (attachment._links && attachment._links.self && attachment._links.self.href) {
				attachmentLinks.push(attachment._links.self.href)
			}
		});

		return attachmentLinks
	}

	private async getScriptDefinitions() {
		let metadataContents = await _currentInfoTab.getAttachmentContents('metadata')
		let metadataJSON: string = metadataContents[0]
		_currentInfoTab.toBeIncludedInHTML.push(_currentInfoTab.createScriptWithContent(`jgivenReport.metaData = ${metadataJSON}`))

		let tagsContents = await _currentInfoTab.getAttachmentContents('tags')
		_currentInfoTab.toBeIncludedInHTML.push(_currentInfoTab.createScriptWithContent(`jgivenReport.setTags(${tagsContents[0]})`))

		let dataContents = await _currentInfoTab.getAttachmentContents('data')
		$.each(dataContents, (index, dataJSONContent) => {
			for (let localPath of _currentInfoTab.locationTranslation.keys()) {
				let from: number = 0
				let startingIndex: number = dataJSONContent.indexOf(localPath, from)
				while (startingIndex != -1) {
					let localPathLength: number = localPath.length
					let newLink: string = _currentInfoTab.locationTranslation.get(localPath)
					dataJSONContent = dataJSONContent.slice(0, startingIndex) + newLink + dataJSONContent.slice(startingIndex + localPathLength, dataJSONContent.length)
					from = startingIndex + newLink.length
					startingIndex = dataJSONContent.indexOf(localPath, from)
				}
			}
			_currentInfoTab.toBeIncludedInHTML.push(_currentInfoTab.createScriptWithContent(`jgivenReport.addScenarios(${dataJSONContent});`))
		})
	}

	private createScriptWithContent(content: string) {
		let domElement = document.createElement("script");
		domElement.innerHTML = content;
		return domElement;
	}

	private getPlainTextFromArrayBuffer(arrayBuffer: ArrayBuffer): string {
		let decoder = new TextDecoder("utf-8");
		return decoder.decode(arrayBuffer);
	}

	private _initBuildInfo(build: TFS_Build_Contracts.Build) {

	}
}

InfoTab.enhance(InfoTab, $(".jgiven-report"), {});

VSS.notifyLoadSucceeded();
