package com.monitor

import com.amazonaws.services.cloudwatch.*
import com.amazonaws.services.cloudwatch.model.*
import com.amazonaws.auth.*
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration

class CloudWatchMetricPublisher {

	String owner = "dev"
	AmazonCloudWatchClient awsCloudwatchClient

	CloudWatchMetricPublisher(){
		String endpoint = "monitoring.us-west-2.amazonaws.com"
		String signingRegion = "us-west-2"

		awsCloudwatchClient = new AmazonCloudWatchClient(new PropertiesCredentials(new File("/opt/aws/awsCredentials")))
		
		def AmazonCloudWatchClientBuilder builder =	AmazonCloudWatchClientBuilder.defaultClient();
		
		builder.setEndpointConfiguration(new EndpointConfiguration(endpoint, signingRegion));
	}

	CloudWatchMetricPublisher(String region, String credentialsFile){
		String endpoint = "monitoring." + region + ".amazonaws.com"
		String signingRegion = region

		//awsCloudwatchClient = new AmazonCloudWatchClient(new PropertiesCredentials(new File(credentialsFile)))
		AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(new PropertiesCredentials(new File(credentialsFile)))
		awsCloudwatchClient = AmazonCloudWatchClientBuilder.standard()
		.withEndpointConfiguration(new EndpointConfiguration(endpoint, signingRegion))
		.withCredentials(awsStaticCredentialsProvider)
		.build()
	}
		
	public def publishMetrics(latency) {
		PutMetricDataRequest putMetricDataRequest
		
		def data =  collectMetrics(latency)
		
		//Print data
		data.each { println it }

		//Create request
		//Each request can have up to 20 data points
		data.collate(20).each { piece ->
			putMetricDataRequest = new PutMetricDataRequest()
			putMetricDataRequest.setMetricData(buildMetricDataRequest(piece))
			putMetricDataRequest.setNamespace("DFW/EFS/${owner}")

			//Send metrics to Cloudwatch
			awsCloudwatchClient.putMetricData(putMetricDataRequest)
		}
	}

	def collectMetrics(latency) {

		def metrics = []
		metrics <<  [ name: "Latency", value: latency, unit: StandardUnit.Milliseconds ]

		return metrics
	}
	
	def buildMetricDataRequest(data) {
		def metricData = []
		def timestamp = new Date()

		data.each{ it ->
			def datum = new MetricDatum()
			datum.withTimestamp(timestamp)
			datum.withMetricName(it.name)
			datum.withValue(it.value)
			datum.withUnit(it.unit)

			metricData << datum
		}

		return metricData
	}

}
