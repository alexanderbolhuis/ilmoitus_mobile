//
//  Attachment.m
//  Ilmoitus
//
//  Created by Sjors Boom on 13/05/14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "Attachment.h"

@implementation Attachment

-(void)setAttachmentData:(NSData *)dataObject
{
    _data = [dataObject base64EncodedStringWithOptions:0];
}

+(NSData *)ImageToNSData:(UIImage *)image
{
    return UIImageJPEGRepresentation(image, 1.0f);
}

@end
