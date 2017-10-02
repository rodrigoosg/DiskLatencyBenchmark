package com.monitor

import com.amazonaws.services.cloudwatch.*
import com.amazonaws.services.cloudwatch.model.*
import com.amazonaws.auth.*
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration

class CloudWatchMetricPublisher {

	String dimension
	String namespace
	AmazonCloudWatchClient awsCloudwatchClient

	CloudWatchMetricPublisher(){
		String endpoint = "monitoring.us-west-2.amazonaws.com"
		String signingRegion = "us-west-2"
		dimension = "default"
		namespace = "DFW/EFS/DEFAULT"

		AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(new PropertiesCredentials(new File(credentialsFile)))
		awsCloudwatchClient = AmazonCloudWatchClientBuilder.standard()
		.withEndpointConfiguration(new EndpointConfiguration(endpoint, signingRegion))
		.withCredentials(awsStaticCredentialsProvider)
		.build()
	}

	CloudWatchMetricPublisher(String region, String credentialsFile, String dimensionParam, String namespaceParam){
		String endpoint = "monitoring." + region + ".amazonaws.com"
		String signingRegion = region
		dimension = dimensionParam
		namespace = namespaceParam

		AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(new PropertiesCredentials(new File(credentialsFile)))
		awsCloudwatchClient = AmazonCloudWatchClientBuilder.standard()
		.withEndpointConfiguration(new EndpointConfiguration(endpoint, signingRegion))
		.withCredentials(awsStaticCredentialsProvider)
		.build()
	}
		
	public def publishMetric(latency) {
		PutMetricDataRequest putMetricDataRequest
		
		def data =  collectMetric(latency)
		
		//Print data
		//data.each { println it }

		//Create request
		//Each request can have up to 20 data points
		data.collate(20).each { piece ->
			putMetricDataRequest = new PutMetricDataRequest()
			putMetricDataRequest.setMetricData(buildMetricDataRequest(piece))
			putMetricDataRequest.setNamespace(namespace)

			//Send metrics to Cloudwatch
			awsCloudwatchClient.putMetricData(putMetricDataRequest)
		}
	}

	public def publishMetrics(List metricsList) {
		PutMetricDataRequest putMetricDataRequest
		
		def data =  collectMetrics(metricsList)
		
		//Print data
		//data.each { println it }

		//Create request
		//Each request can have up to 20 data points
		data.collate(20).each { piece ->
			putMetricDataRequest = new PutMetricDataRequest()
			putMetricDataRequest.setMetricData(buildMetricDataRequest(piece))
			putMetricDataRequest.setNamespace(namespace)

			//Send metrics to Cloudwatch
			awsCloudwatchClient.putMetricData(putMetricDataRequest)
		}
	}
	
	def collectMetric(latency) {

		def metrics = []
		metrics <<  [ name: "Latency", value: latency, unit: StandardUnit.Milliseconds, dimension: dimension ]

		return metrics
	}

	def collectMetrics(List metricsList) {
		
		double LESS_THAN_50_MILLISECONDS_PERCENTAGE = 0;
		double BETWEEN_50_AND_100_MILLISECONDS_PERCENTAGE = 0;
		double BETWEEN_100_AND_500_MILLISECONDS_PERCENTAGE = 0;
		double BETWEEN_500_AND_1000_MILLISECONDS_PERCENTAGE = 0;
		double BETWEEN_1000_AND_5000_MILLISECONDS_PERCENTAGE = 0;
		double BETWEEN_5000_AND_10000_MILLISECONDS_PERCENTAGE = 0;
		double MORE_THAN_10000_MILLISECONDS_PERCENTAGE = 0;
		
		def metrics = []
		
		metrics <<  [ name: "LessThan50MillisecondsPercentage", value: metricsList[0], unit: StandardUnit.Percent, dimension: dimension ]
		metrics <<  [ name: "Between50And100MillisecondsPercentage", value: metricsList[1], unit: StandardUnit.Percent, dimension: dimension ]
		metrics <<  [ name: "Between100And500MillisecondsPercentage", value: metricsList[2], unit: StandardUnit.Percent, dimension: dimension ]
		metrics <<  [ name: "Between500And1000MillisecondsPercentage", value: metricsList[3], unit: StandardUnit.Percent, dimension: dimension ]
		metrics <<  [ name: "Between1000And5000MillisecondsPercentage", value: metricsList[4], unit: StandardUnit.Percent, dimension: dimension ]
		metrics <<  [ name: "Between5000And10000MillisecondsPercentage", value: metricsList[5], unit: StandardUnit.Percent, dimension: dimension ]
		metrics <<  [ name: "MoreThan10000MillisecondsPercentage", value: metricsList[6], unit: StandardUnit.Percent, dimension: dimension ]
				
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
