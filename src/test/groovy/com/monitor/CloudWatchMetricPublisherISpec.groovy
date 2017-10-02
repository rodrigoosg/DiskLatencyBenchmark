/**
 * Created by Rodrigo on 2/17/2016.
 */
package com.monitor

import spock.lang.Specification;

class CloudWatchMetricPublisherISpec extends Specification {

	CloudWatchMetricPublisher cloudWatchMetricPublisher
	
	def setup() {
		cloudWatchMetricPublisher = new CloudWatchMetricPublisher("us-west-2", "resources/awsCredentials", "LOCAL-TESTS", "DFW/EFS/TESTS")
	}
	
	def "should execute metric publication"() {
		when:
		def publishedResult = cloudWatchMetricPublisher.publishMetric(20)
		
		then:
		publishedResult.toString() == [[[name:"Latency", value:"20", unit:"Milliseconds", dimension: "LOCAL-TESTS"]]].toString()
	}

	def "should execute publish metrics method"() {
		when:
		def publishedResult = cloudWatchMetricPublisher.publishMetrics([50,30,45,30,55,33,70])
		
		then:
		publishedResult.toString().contains("LessThan50MillisecondsPercentage")
	}
		
}