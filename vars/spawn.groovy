def call(Map args = [:], body = null){
    if (args.commands == null && body == null) {
        error "Please specify either command or body; aborting ..."
        currentBuild.result = 'ABORTED'
        return
    }

    def spec = specForImage(args.image, args.version?: 'latest')
    def checkoutScm = args.checkout_scm ?: true

    // oc is available on master so don't spawn unnecessarily
    if (args.image == "oc") {
      execute(args.commands, body)
      return
    }

    pod(name: args.image, image: spec.image, shell: spec.shell) {
      if (checkoutScm) {
        checkout scm
      }

      execute(args.commands, body)
    }
}

def execute(commands, body) {
  if (commands) {
    sh commands
  }

  if (body) {
    body()
  }
}

def specForImage(image, version){
  // TODO use proper images
  def specs = [
    "golang": [
      "latest": [
            image: "sbose78/jenkins-agent-golang-1.10-centos7",
            shell: '/bin/bash'
      ],
      "1.10": [
            image: "sbose78/jenkins-agent-golang-1.10-centos7",
            shell: '/bin/bash'
      ],
    ],
    "node": [
      "latest": [
            image: "openshift/jenkins-agent-nodejs-8-centos7",
            shell: '/bin/bash'
        ],
      "8.9": [
            image: "openshift/jenkins-agent-nodejs-8-centos7",
            shell: '/bin/bash'
      ],
      "4.6": [
            image: "openshift/jenkins-slave-nodejs-centos7",
            shell: '/bin/bash'
      ],
    ],
    "oc": [
      "latest": [
            image: "openshift/jenkins-agent-nodejs-8-centos7",
            shell: '/bin/bash'
      ],
      "3.11": [
            image: "openshift/jenkins-agent-nodejs-8-centos7",
            shell: '/bin/bash'
      ],
    ],
    "java": [
      "latest": [
            image: "openshift/jenkins-slave-maven-centos7:v4.0",
            shell: '/bin/bash'
      ],
      "1.8": [
            image: "openshift/jenkins-slave-maven-centos7:v4.0",
            shell: '/bin/bash'
      ],
    ],
  ]

  // TODO: validate image in specs
  return specs[image][version]
}
