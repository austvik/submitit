package no.java.submitit.app

private[app] object DefaultConfigValues {
  
   val configValues = Map(
		"smtpHost" -> null,
		"submitAllowedBoolean" -> "true",
		"editPageInfoTextHtml" -> """<ul><li>Click the "Help" link, or press the question mark at each field for information about what to enter.</li><li>Before you submit your presentation you have to review it by pressing the "Review presentation" link.</li></ul>""",
		"captchaLengthInt" -> "1",
		"reviewPageViewSubmittedChangeAllowedHthml" -> """You can still change the contents of your submission. You may edit by pressing the "Edit link".<br>If you have any questions, please email <a href="mailto:program@java.no">program@java.no</a>""",
		"emsUrl" ->  null,
		"globalEditAllowedBoolean" -> "true",
		"emsPwd" -> null,
		"emsUser" -> null,
		"reviewPageViewSubmittedHthml" -> """If you have any questions, please email <a href="mailto:program@java.no">program@java.no</a>""",
		"adminPassPhrase" -> null,
		"reviewPageBeforeSubmitHtml" -> """Your presentation has not yet been submittet. Please review, and press the "Submit presentation" link when you are ready.""",
		"eventName" -> """JavaZone 2009""",
		"headerText" -> """Submit your JavaZone 2009 presentation""",
		"submitNotAllowedHtml" -> """Call for papers is currently not open.""",
		"emailBccCommaSeparatedList" -> null,
		"officialEmailReplyTo" -> """program@java.no""",
		"submititBaseUrl" -> """http://localhost:8080""",
		"feedbackRejected" -> """Unfortunately we could not provide a presentation slot for this presentation. We have gotten more than 200 proposals this year, selecting the best have been difficult. If you have other submissions you should check their status as well. If you have any questions you may email program@java.no.""",
		"allowSlideUploadBoolen" -> "false",
		"passPhraseSubmitSpecialURL" -> """jz""",
		"showFeedbackBoolean" -> "false",
		"showSpecialMessageOnRejectBoolean" -> "false",
		"allowIndidualFeedbackOnRejectBoolean" -> "false",
		"showActualStatusInReviewPageBoolean" -> "false",
		"userSelectedKeywords" -> "Keyword1, Keyword2, Keyword3, Keyword4, Keyword5, Keyword6 , Keyword7, Keyword 8, Keyword 9, Keyword 10",
		"showUserSelectedKeywords" -> "true"
  )

}