package com.monitor

import com.amazonaws.services.cloudwatch.*
import com.amazonaws.services.cloudwatch.model.*
import com.amazonaws.auth.*
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration

class CloudWatchMetricPublisher {

	String owner
	AmazonCloudWatchClient awsCloudwatchClient

	CloudWatchMetricPublisher(){
		String endpoint = "monitoring.us-west-2.amazonaws.com"
		String signingRegion = "us-west-2"
		owner = "default"

		AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(new PropertiesCredentials(new File(credentialsFile)))
		awsCloudwatchClient = AmazonCloudWatchClientBuilder.standard()
		.withEndpointConfiguration(new EndpointConfiguration(endpoint, signingRegion))
		.withCredentials(awsStaticCredentialsProvider)
		.build()
	}

	CloudWatchMetricPublisher(String region, String credentialsFile, String ownerParam){
		String endpoint = "monitoring." + region + ".amazonaws.com"
		String signingRegion = region
		owner = ownerParam

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
		//data.each { println it }

		//Create request
		//Each request can have up to 20 data points
		data.collate(20).each { piece ->
			putMetricDataRequest = new PutMetricDataRequest()
			putMetricDataRequest.setMetricData(buildMetricDataRequest(piece))
			putMetricDataRequest.setNamespace("DFW/EFS")

			//Send metrics to Cloudwatch
			awsCloudwatchClient.putMetricData(putMetricDataRequest)
		}
	}

	def collectMetrics(latency) {

		def metrics = []
		metrics <<  [ name: "Latency", value: latency, unit: StandardUnit.Milliseconds, dimension: owner ]

		return metrics
	}
	
	def buildMetricDataRequest(data) {
		def metricData = []
		def timestamp = new Date()

		data.each{ it ->
			
			Dimension dimension = new Dimension()
			.withName("EFS Custom Metrics")
			.withValue(it.dimension);
			
			def datum = new MetricDatum()
			datum.withTimestamp(timestamp)
			datum.withMetricName(it.name)
			datum.withValue(it.value)
			datum.withUnit(it.unit)
			datum.withDimensions(dimension)

			metricData << datum
		}

		return metricData
	}

}
