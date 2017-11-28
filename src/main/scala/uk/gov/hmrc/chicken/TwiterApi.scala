package uk.gov.hmrc.chicken

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{IDs, PagableResponseList, TwitterFactory}

class TwiterApi {

  val twitterHandle = "HMRChicken"

  val cb = new ConfigurationBuilder()
  cb.setDebugEnabled(true)
    .setOAuthConsumerKey("6tcfXJqEAbVfIkJ6vkeR78zcx")
    .setOAuthConsumerSecret("Dy9twXDnVBT7mnMoaqqytw5LjoJ1cJPT6BaRsHYVbmBnfUEpV1")
    .setOAuthAccessToken("935453835295952896-9rmBQRuBxaUxz0vLYcZGYfeFTPpPacv")
    .setOAuthAccessTokenSecret("oLYCPQM9Zx5f9lcdnSdJ5onTCehNVoBmuXjMepyvNVm3M")
  val tf = new TwitterFactory(cb.build())
  val twtr = tf.getInstance()

  def post(message: String): Unit = {
    twtr.updateStatus(message)
  }

  def dm(recipient: String, message: String): Unit = {
    twtr.sendDirectMessage(recipient, message)
  }

  def followers: PagableResponseList[twitter4j.User] = twtr.getFollowersList(twitterHandle, -1)
//  def followers: IDs = twtr.getFollowersIDs(twitterHandle, -1)

}
