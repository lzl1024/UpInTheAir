import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteNotificationConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeletePolicyRequest;
import com.amazonaws.services.autoscaling.model.InstanceMonitoring;
import com.amazonaws.services.autoscaling.model.PutNotificationConfigurationRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyResult;
import com.amazonaws.services.autoscaling.model.Tag;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Listener;

/**
 * Add frontend ASG.
 * 
 * @author Yinsu Chu (yinsuc)
 * 
 */
public class FrontEndASG {
	private static final String AMI = "ami-8b9dbbe2";
	private static final String CREDENTIAL_FILE = "./AwsCredentials.properties";
	private static final String SECURITY_GROUP = "Everything";
	private static final String INSTANCE_TYPE = "m1.small";
	private static final String AVAILABILITY_ZONE = "us-east-1a";
	private static final String LB_NAME = "FrontEndASGLoadBalancer";
	private static final String AS_GROUP_NAME = "FrontEndASGAutoScalingGroup";
	private static final String LAUNCH_CONFIG_NAME = "FrontEndASGLaunchConfiguration";
	private static final String SCALE_OUT_POLICY_NAME = "ScaleOut";
	private static final int SCALE_OUT_NUM = 3;
	private static final String SCALE_IN_POLICY_NAME = "ScaleIn";
	private static final int SCALE_IN_NUM = -3;
	private static final String METRIC_NAME = "CPUUtilization";
	private static final String ADJUSTMENT_TYPE = "ChangeInCapacity";
	private static final String NAMESPACE = "AWS/EC2";
	private static final int ALARM_PERIOD = 180;
	private static final String ALARM_SCALE_IN = "ScaleInAlarm";
	private static final String ALARM_SCALE_OUT = "ScaleOutAlarm";
	private static final String SNS_ARN = "arn:aws:sns:us-east-1:583641942128:FrontEndASG";
	private static final String[] NOTIFICATION_TYPES = {
			"autoscaling:EC2_INSTANCE_LAUNCH",
			"autoscaling:EC2_INSTANCE_LAUNCH_ERROR",
			"autoscaling:EC2_INSTANCE_TERMINATE",
			"autoscaling:EC2_INSTANCE_TERMINATE_ERROR",
			"autoscaling:TEST_NOTIFICATION" };
	private static final int HC_HEALTHY_THRESHOLD = 10;
	private static final int HC_INTERVAL = 30;
	private static final int HC_TIMEOUT = 5;
	private static final int HC_UNHEALTHY_THRESHOLD = 2;
	private static final String HC_TARGET = "HTTP:80/q1";
	private static final int MIN_NUMBER = 2;
	private static final int MAX_NUMBER = 14;

	private AmazonElasticLoadBalancingClient elb;
	private AmazonAutoScalingClient as;
	private AmazonCloudWatchClient cw;
	private BasicAWSCredentials bawsc;

	public FrontEndASG() {
		Properties properties = new Properties();
		try {
			properties.load(FrontEndASG.class
					.getResourceAsStream(CREDENTIAL_FILE));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		}
		bawsc = new BasicAWSCredentials(properties.getProperty("accessKey"),
				properties.getProperty("secretKey"));
		elb = new AmazonElasticLoadBalancingClient(bawsc);
		as = new AmazonAutoScalingClient(bawsc);
		cw = new AmazonCloudWatchClient(bawsc);
	}

	private String createELB() {
		CreateLoadBalancerRequest elbRequest = new CreateLoadBalancerRequest();
		elbRequest.setLoadBalancerName(LB_NAME);
		List<Listener> listeners = new ArrayList<Listener>();
		listeners.add(new Listener("HTTP", 80, 80));
		List<String> availabilityZones = new ArrayList<String>();
		availabilityZones.add(AVAILABILITY_ZONE);
		elbRequest.setListeners(listeners);
		elbRequest.setAvailabilityZones(availabilityZones);
		CreateLoadBalancerResult elbResult = elb.createLoadBalancer(elbRequest);

		HealthCheck hc = new HealthCheck();
		hc.setTarget(HC_TARGET);
		hc.setHealthyThreshold(HC_HEALTHY_THRESHOLD);
		hc.setInterval(HC_INTERVAL);
		hc.setTimeout(HC_TIMEOUT);
		hc.setUnhealthyThreshold(HC_UNHEALTHY_THRESHOLD);

		ConfigureHealthCheckRequest hcRequest = new ConfigureHealthCheckRequest(
				LB_NAME, hc);
		elb.configureHealthCheck(hcRequest);

		return elbResult.getDNSName();
	}

	private void createAutoScalingGroup() {
		CreateAutoScalingGroupRequest request = new CreateAutoScalingGroupRequest();
		List<String> avZones = new ArrayList<String>();
		avZones.add(AVAILABILITY_ZONE);
		List<String> elbs = new ArrayList<String>();
		elbs.add(LB_NAME);

		request.setAutoScalingGroupName(AS_GROUP_NAME);
		request.setLaunchConfigurationName(LAUNCH_CONFIG_NAME);
		request.setAvailabilityZones(avZones);
		request.setMinSize(MIN_NUMBER);
		request.setMaxSize(MAX_NUMBER);
		request.setDesiredCapacity(MIN_NUMBER);
		request.setLoadBalancerNames(elbs);
		ArrayList<Tag> tags = new ArrayList<Tag>();
		Tag tag1 = new Tag().withKey("15619_Group").withValue("Up_in_the_Air");
		Tag tag2 = new Tag().withKey("15619_Step").withValue("4");
		tags.add(tag1);
		tags.add(tag2);
		request.setTags(tags);

		as.createAutoScalingGroup(request);
	}

	private void createLaunchConfiguration() {
		CreateLaunchConfigurationRequest request = new CreateLaunchConfigurationRequest();
		List<String> sg = new ArrayList<String>();
		sg.add(SECURITY_GROUP);
		InstanceMonitoring monitoring = new InstanceMonitoring();
		monitoring.setEnabled(true);

		request.setLaunchConfigurationName(LAUNCH_CONFIG_NAME);
		request.setSecurityGroups(sg);
		request.setImageId(AMI);
		request.setInstanceType(INSTANCE_TYPE);
		request.setInstanceMonitoring(monitoring);

		as.createLaunchConfiguration(request);
	}

	private String createScalingPolicy(String policyName, int scalingNumber) {
		PutScalingPolicyRequest request = new PutScalingPolicyRequest();

		request.setAutoScalingGroupName(AS_GROUP_NAME);
		request.setPolicyName(policyName);
		request.setAdjustmentType(ADJUSTMENT_TYPE);
		request.setScalingAdjustment(scalingNumber);

		PutScalingPolicyResult result = as.putScalingPolicy(request);

		return result.getPolicyARN();
	}

	private void createAlarm(String alarmName, String policyARN,
			ComparisonOperator operator, double threshold, int period,
			int evaluationPeriods) {
		PutMetricAlarmRequest request = new PutMetricAlarmRequest();
		List<Dimension> dimensions = new ArrayList<Dimension>();
		Dimension d = new Dimension();
		d.setName("AutoScalingGroupName");
		d.setValue(AS_GROUP_NAME);
		dimensions.add(d);
		List<String> actions = new ArrayList<String>();
		actions.add(policyARN);

		request.setAlarmName(alarmName);
		request.setMetricName(METRIC_NAME);
		request.setDimensions(dimensions);
		request.setNamespace(NAMESPACE);
		request.setComparisonOperator(operator);
		request.setStatistic(Statistic.Average);
		request.setUnit(StandardUnit.Percent);
		request.setThreshold(threshold);
		request.setPeriod(period);
		request.setEvaluationPeriods(evaluationPeriods);
		request.setAlarmActions(actions);

		cw.putMetricAlarm(request);
	}

	private void createNotification() {
		PutNotificationConfigurationRequest request = new PutNotificationConfigurationRequest();
		List<String> types = Arrays.asList(NOTIFICATION_TYPES);

		request.setAutoScalingGroupName(AS_GROUP_NAME);
		request.setTopicARN(SNS_ARN);
		request.setNotificationTypes(types);

		as.putNotificationConfiguration(request);
	}

	private void deleteASPolicy(String policyName) {
		DeletePolicyRequest deletePolicy = new DeletePolicyRequest();

		deletePolicy.setAutoScalingGroupName(AS_GROUP_NAME);
		deletePolicy.setPolicyName(policyName);

		as.deletePolicy(deletePolicy);
	}

	private void deleteASNotification() {
		DeleteNotificationConfigurationRequest deleteNotification = new DeleteNotificationConfigurationRequest();

		deleteNotification.setAutoScalingGroupName(AS_GROUP_NAME);
		deleteNotification.setTopicARN(SNS_ARN);

		as.deleteNotificationConfiguration(deleteNotification);
	}

	private void deleteASLaunchConfiguration() {
		DeleteLaunchConfigurationRequest request = new DeleteLaunchConfigurationRequest();

		request.setLaunchConfigurationName(LAUNCH_CONFIG_NAME);

		as.deleteLaunchConfiguration(request);
	}

	private void terminateASGroup() {
		DeleteAutoScalingGroupRequest request = new DeleteAutoScalingGroupRequest();

		deleteASPolicy(SCALE_OUT_POLICY_NAME);
		System.out.println("scale out policy deleted");
		deleteASPolicy(SCALE_IN_POLICY_NAME);
		System.out.println("scale in policy deleted");
		deleteASNotification();
		System.out.println("notification deleted");

		request.setAutoScalingGroupName(AS_GROUP_NAME);
		request.setForceDelete(true);
		as.deleteAutoScalingGroup(request);
		System.out.println("scaling group deleted");

		deleteASLaunchConfiguration();
		System.out.println("launch configuration deleted");
	}

	public static void main(String[] args) {
		FrontEndASG fea = new FrontEndASG();

		fea.createELB();
		System.out.println("ELB created");
		fea.createLaunchConfiguration();
		System.out.println("launch configuration created");
		fea.createAutoScalingGroup();
		System.out.println("autoscaling group created");
		fea.createNotification();
		System.out.println("notification created");
		String scaleOutARN = fea.createScalingPolicy(SCALE_OUT_POLICY_NAME,
				SCALE_OUT_NUM);
		System.out.println("scaling out policy created");
		String scaleInARN = fea.createScalingPolicy(SCALE_IN_POLICY_NAME,
				SCALE_IN_NUM);
		System.out.println("scaling in policy created");
		fea.createAlarm(ALARM_SCALE_OUT, scaleOutARN,
				ComparisonOperator.GreaterThanThreshold, 60D, ALARM_PERIOD, 1);
		System.out.println("scaling out alarm created");
		fea.createAlarm(ALARM_SCALE_IN, scaleInARN,
				ComparisonOperator.LessThanThreshold, 20D, ALARM_PERIOD, 1);
		System.out.println("scaling in alarm created");

		// fea.terminateASGroup();
	}
}
